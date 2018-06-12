/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.util.JSONPObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.farm.Farm;
import org.thingsboard.server.common.data.farm.FarmSearchQuery;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.FarmId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.TextPageData;
import org.thingsboard.server.common.data.page.TextPageLink;
import org.thingsboard.server.common.data.parcel.Parcel;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.nosql.FarmEntity;
import org.thingsboard.server.dao.model.nosql.ParcelEntity;
import org.thingsboard.server.dao.model.nosql.TenantEntity;
import org.thingsboard.server.exception.ThingsboardErrorCode;
import org.thingsboard.server.exception.ThingsboardException;
import org.thingsboard.server.service.security.model.SecurityUser;

import javax.imageio.ImageIO;

/**
 *
 * @author German Lopez
 */
@RestController
@RequestMapping("/api")
public class FarmController extends BaseController {
   

    public static final String FARM_ID = "farmId";

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/farm/{farmId}", method = RequestMethod.GET)
    @ResponseBody
    public Farm getFarmById(@PathVariable(FARM_ID) String strFarmId) throws ThingsboardException {
        checkParameter(FARM_ID, strFarmId);
        try {
            FarmId farmId = new FarmId(toUUID(strFarmId));
            return checkFarmId(farmId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/farm/{farmId}/climatology/{lon}/{lat}", method = RequestMethod.GET)
    @ResponseBody
    public String getFarmClimatology(@PathVariable String farmId,@PathVariable String lon,@PathVariable String lat) throws IOException {
        String url = "http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid=3edc2d35729187eaadfb70a0443f5c74";
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return jsonText;
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }


    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/farm", method = RequestMethod.POST)
    @ResponseBody
    public Farm saveFarm(@RequestBody Farm farm) throws ThingsboardException {
        try {
            farm.setTenantId(getCurrentUser().getTenantId());
            if (getCurrentUser().getAuthority() == Authority.CUSTOMER_USER) {
                if (farm.getId() == null || farm.getId().isNullUid() ||
                    farm.getCustomerId() == null || farm.getCustomerId().isNullUid()) {
                    throw new ThingsboardException("You don't have permission to perform this operation!",
                            ThingsboardErrorCode.PERMISSION_DENIED);
                } else {
                    checkCustomerId(farm.getCustomerId());
                }
            }
            //System.out.println("Hay foto: "+farm.getFarmPhotographs().getFront().exists());
            //Adding a new dashboard with farm name----------------------------------------
            List<TenantEntity> lT = tenantService.findTenantByTitle().get();
            Tenant t = lT.get(0).toData();
            TenantId tenantId = new TenantId(t.getUuidId());
            Dashboard dashboard = new Dashboard();
            dashboard.setTenantId(tenantId);
            String dashboardJson = "{\"description\":"+'"'+farm.getName()+'"'+",\"widgets\":{},\"states\":{\"default\":{\"name\":"+'"'+farm.getName()+'"'+",\"root\":true,\"layouts\":{\"main\":{\"widgets\":{},\"gridSettings\":{\"backgroundColor\":\"#eeeeee\",\"color\":\"rgba(0,0,0,0.870588)\",\"columns\":24,\"margins\":[10,10],\"backgroundSizeMode\":\"100%\"}}}}},\"entityAliases\":{},\"timewindow\":{\"displayValue\":\"\",\"selectedTab\":0,\"realtime\":{\"interval\":1000,\"timewindowMs\":60000},\"history\":{\"historyType\":0,\"interval\":1000,\"timewindowMs\":60000,\"fixedTimewindow\":{\"startTimeMs\":1520656350529,\"endTimeMs\":1520742750529}},\"aggregation\":{\"type\":\"AVG\",\"limit\":200}},\"settings\":{\"stateControllerId\":\"entity\",\"showTitle\":false,\"showDashboardsSelect\":true,\"showEntitiesSelect\":true,\"showDashboardTimewindow\":true,\"showDashboardExport\":true,\"toolbarAlwaysOpen\":true}}";
            ObjectMapper mapper = new ObjectMapper();
            JsonNode configuration = mapper.readTree(dashboardJson);
            dashboard.setConfiguration(configuration);
            dashboard.setTitle(farm.getName());
            Dashboard savedDashboard = dashboardService.saveDashboard(dashboard);
            //------------------------------------------------------------
            farm.setDashboardId(savedDashboard.getId().getId().toString());
            Farm savedFarm  = checkNotNull(farmService.saveFarm(farm));
            SpatialFarm spatialFarm = new SpatialFarm(savedFarm.getId().getId().toString(),savedFarm.getName(),farm.getLocation());
            mongoService.getMongodbFarm().save(spatialFarm);

            logEntityAction(savedFarm.getId(), savedFarm,
                    savedFarm.getCustomerId(),
                    farm.getId() == null ? ActionType.ADDED : ActionType.UPDATED, null);

            return  savedFarm;
        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.FARM), farm,
                    null, farm.getId() == null ? ActionType.ADDED : ActionType.UPDATED, e);
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/farm/front/{farmId}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void uploadFile(MultipartHttpServletRequest request, @PathVariable(FARM_ID) String farmId) throws ThingsboardException {
        try {
            System.out.println("Entró al back-end");
            Iterator<String> itr = request.getFileNames();
            HashMap<String,String> metadata = new HashMap<>();
            while (itr.hasNext()) {
                String uploadedFile = itr.next();
                MultipartFile file = request.getFile(uploadedFile);
                System.out.println("Archivo");
                File imagen = new File(file.getOriginalFilename());
                imagen.createNewFile();
                FileOutputStream fos = new FileOutputStream(imagen);
                fos.write(file.getBytes());
                fos.close();
                metadata.put("FarmId",farmId);
                System.out.println("Antes de la conversión");
                File filetemp;
                if (file.getOriginalFilename().endsWith(".tif")){
                    final BufferedImage tif = ImageIO.read(imagen);
                    String temp= file.getOriginalFilename().replace(".tif",".png");
                    filetemp= new File(temp);
                    ImageIO.write(tif, "png", filetemp);
                }
                else{
                    filetemp=imagen;
                }
                System.out.println("Antes de enviar al mongo");
                mongoService.getMongodbimage().uploadFile(filetemp,metadata);

                System.out.println("Después de que mongo guardo la imagen");
                filetemp.delete();
            }
        } catch (Exception e) {
            throw handleException(e);
        }

    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/farm/multipleImage", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void uploadMultipleImage(@RequestParam(value = "uploadedFile", required = false) MultipartFile[] request) throws ThingsboardException {
        System.out.println(request.length);
        try {
            for(MultipartFile f: request){
                File imagen = new File(f.getOriginalFilename());
                imagen.createNewFile();
                FileOutputStream fos = new FileOutputStream(imagen);
                fos.write(f.getBytes());
                fos.close();
                System.out.println(imagen.getName());
                System.out.println(imagen.getTotalSpace());
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /*@PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/farm/front/{farmId}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public File loadFile(@PathVariable(FARM_ID) String farmId) throws ThingsboardException {
        try {
           return mongoService.getMongodbimage().getFrontImage(farmId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }*/

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/farm/front/{farmId}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody Map<String, String> getImage(@PathVariable String farmId) throws ThingsboardException {
        try {
            String file = mongoService.getMongodbimage().downloadFile(farmId);
            //File file = new ClassPathResource(imagesPath + imageName).getFile();
            //String encodeImage = Base64.getEncoder().withoutPadding().encodeToString(Files.readAllBytes(file.toPath()));
            Map<String, String> jsonMap = new HashMap<>();
            jsonMap.put("content", file);
            return jsonMap;
        } catch (Exception e) {
           throw handleException(e);
        }

    }



    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/farm/{farmId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteFarm(@PathVariable(FARM_ID) String strFarmId) throws ThingsboardException {
        checkParameter(FARM_ID, strFarmId);
        try {
            FarmId farmId = new FarmId(toUUID(strFarmId));
            Farm farm = checkFarmId(farmId);
            farmService.deleteFarm(farmId);
            mongoService.getMongodbFarm().removeById(strFarmId);
            logEntityAction(farmId, farm,
                    farm.getCustomerId(),
                    ActionType.DELETED, null, strFarmId);

        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.FARM),
                    null,
                    null,
                    ActionType.DELETED, e, strFarmId);
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/{customerId}/farm/{farmId}", method = RequestMethod.POST)
    @ResponseBody
    public Farm assignFarmToCustomer(@PathVariable("customerId") String strCustomerId,
                                       @PathVariable(FARM_ID) String strFarmId) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        checkParameter(FARM_ID, strFarmId);
        try {
            CustomerId customerId = new CustomerId(toUUID(strCustomerId));
            Customer customer = checkCustomerId(customerId);

            FarmId farmId = new FarmId(toUUID(strFarmId));
            checkFarmId(farmId);

            Farm savedFarm = checkNotNull(farmService.assignFarmToCustomer(farmId, customerId));

            logEntityAction(farmId, savedFarm,
                    savedFarm.getCustomerId(),
                    ActionType.ASSIGNED_TO_CUSTOMER, null, strFarmId, strCustomerId, customer.getName());

            return  savedFarm;
        } catch (Exception e) {

            logEntityAction(emptyId(EntityType.FARM), null,
                    null,
                    ActionType.ASSIGNED_TO_CUSTOMER, e, strFarmId, strCustomerId);

            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/farm/{farmId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Farm unassignFarmFromCustomer(@PathVariable(FARM_ID) String strFarmId) throws ThingsboardException {
        checkParameter(FARM_ID, strFarmId);
        try {
            FarmId farmId = new FarmId(toUUID(strFarmId));
            Farm farm = checkFarmId(farmId);
            if (farm.getCustomerId() == null || farm.getCustomerId().getId().equals(ModelConstants.NULL_UUID)) {
                throw new IncorrectParameterException("Farm isn't assigned to any customer!");
            }

            Customer customer = checkCustomerId(farm.getCustomerId());

            Farm savedFarm = checkNotNull(farmService.unassignFarmFromCustomer(farmId));

            logEntityAction(farmId, farm,
                    farm.getCustomerId(),
                    ActionType.UNASSIGNED_FROM_CUSTOMER, null, strFarmId, customer.getId().toString(), customer.getName());

            return savedFarm;
        } catch (Exception e) {

            logEntityAction(emptyId(EntityType.FARM), null,
                    null,
                    ActionType.UNASSIGNED_FROM_CUSTOMER, e, strFarmId);

            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/public/farm/{farmId}", method = RequestMethod.POST)
    @ResponseBody
    public Farm assignFarmToPublicCustomer(@PathVariable(FARM_ID) String strFarmId) throws ThingsboardException {
        checkParameter(FARM_ID, strFarmId);
        try {
            FarmId farmId = new FarmId(toUUID(strFarmId));
            Farm farm = checkFarmId(farmId);
            Customer publicCustomer = customerService.findOrCreatePublicCustomer(farm.getTenantId());
            Farm savedFarm = checkNotNull(farmService.assignFarmToCustomer(farmId, publicCustomer.getId()));

            logEntityAction(farmId, savedFarm,
                    savedFarm.getCustomerId(),
                    ActionType.ASSIGNED_TO_CUSTOMER, null, strFarmId, publicCustomer.getId().toString(), publicCustomer.getName());

            return savedFarm;
        } catch (Exception e) {

            logEntityAction(emptyId(EntityType.FARM), null,
                    null,
                    ActionType.ASSIGNED_TO_CUSTOMER, e, strFarmId);

            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/farms", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<Farm> getTenantFarms(
            @RequestParam int limit,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws ThingsboardException {
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            if (type != null && type.trim().length()>0) {
                TextPageData<Farm> farms = farmService.findFarmsByTenantIdAndType(tenantId, type, pageLink);
                for(Farm f : farms.getData()){
                    f.setLocation(mongoService.getMongodbFarm().findById(f.getId().getId().toString()).getPolygons());
                }
                return checkNotNull(farms);
            } else {
                TextPageData<Farm> farms = farmService.findFarmsByTenantId(tenantId, pageLink);
                for(Farm f : farms.getData()){
                    f.setLocation(mongoService.getMongodbFarm().findById(f.getId().getId().toString()).getPolygons());
                }
                return checkNotNull(farms);
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/farms", params = {"farmName"}, method = RequestMethod.GET)
    @ResponseBody
    public Farm getTenantFarm(
            @RequestParam String farmName) throws ThingsboardException {
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            return checkNotNull(farmService.findFarmByTenantIdAndName(tenantId, farmName));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/customer/{customerId}/farms", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<Farm> getCustomerFarms(
            @PathVariable("customerId") String strCustomerId,
            @RequestParam int limit,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            CustomerId customerId = new CustomerId(toUUID(strCustomerId));
            checkCustomerId(customerId);
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            if (type != null && type.trim().length()>0) {
                return checkNotNull(farmService.findFarmsByTenantIdAndCustomerIdAndType(tenantId, customerId, type, pageLink));
            } else {
                return checkNotNull(farmService.findFarmsByTenantIdAndCustomerId(tenantId, customerId, pageLink));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/farms", params = {"farmIds"}, method = RequestMethod.GET)
    @ResponseBody
    public List<Farm> getFarmsByIds(
            @RequestParam("farmIds") String[] strFarmIds) throws ThingsboardException {
        checkArrayParameter("farmIds", strFarmIds);
        try {
            SecurityUser user = getCurrentUser();
            TenantId tenantId = user.getTenantId();
            CustomerId customerId = user.getCustomerId();
            List<FarmId> farmIds = new ArrayList<>();
            for (String strFarmId : strFarmIds) {
                farmIds.add(new FarmId(toUUID(strFarmId)));
            }
            ListenableFuture<List<Farm>> farms;
            if (customerId == null || customerId.isNullUid()) {
                farms = farmService.findFarmsByTenantIdAndIdsAsync(tenantId, farmIds);
                for(Farm f : farms.get()){
                    f.setLocation(mongoService.getMongodbFarm().findById(f.getId().getId().toString()).getPolygons());
                }
            } else {
                farms = farmService.findFarmsByTenantIdCustomerIdAndIdsAsync(tenantId, customerId, farmIds);
                for(Farm f : farms.get()){
                    f.setLocation(mongoService.getMongodbFarm().findById(f.getId().getId().toString()).getPolygons());
                }
            }
            return checkNotNull(farms.get());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/farms", method = RequestMethod.POST)
    @ResponseBody
    public List<Farm> findByQuery(@RequestBody FarmSearchQuery query) throws ThingsboardException {
        checkNotNull(query);
        checkNotNull(query.getParameters());
        checkNotNull(query.getFarmTypes());
        checkEntityId(query.getParameters().getEntityId());
        try {
            List<Farm> farms = checkNotNull(farmService.findFarmsByQuery(query).get());
            farms = farms.stream().filter(farm -> {
                try {
                    checkFarm(farm);
                    return true;
                } catch (ThingsboardException e) {
                    return false;
                }
            }).collect(Collectors.toList());
            return farms;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/farm/types", method = RequestMethod.GET)
    @ResponseBody
    public List<EntitySubtype> getFarmTypes() throws ThingsboardException {
        try {
            SecurityUser user = getCurrentUser();
            TenantId tenantId = user.getTenantId();
            ListenableFuture<List<EntitySubtype>> farmTypes = farmService.findFarmTypesByTenantId(tenantId);
            return checkNotNull(farmTypes.get());
        } catch (Exception e) {
            throw handleException(e);
        }
    }
    
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/Allfarms", method = RequestMethod.GET)
    @ResponseBody
    public List<Farm> getAllFarms() throws ThingsboardException {
        try {
            List<FarmEntity> farmTypes = farmService.allFarms().get();
            List<Farm> farms = new ArrayList<>();
            for(FarmEntity fe : farmTypes){
                farms.add(fe.toData());
            }
            return farms;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/farm/{farmId}/parcels", method = RequestMethod.GET)
    @ResponseBody
    public List<SpatialParcel> getParcelByFarmId(@PathVariable(FARM_ID) String farmId) throws ThingsboardException{
        List<SpatialParcel> parcelsFarmId = new ArrayList<>();
        try {
            List<ParcelEntity> parcelType = parcelService.allParcels().get();
            List<Parcel> parcels = new ArrayList<>();
            for(ParcelEntity p : parcelType){
                if(p.toData().getFarmId().equals(farmId)){
                    parcels.add(p.toData());
                }
            }
            for(Parcel pa: parcels){
                parcelsFarmId.add(mongoService.getMongodbparcel().findById(pa.getId().getId().toString()));
            }
            return parcelsFarmId;

        } catch (Exception e) {
            throw handleException(e);
        }
    }
}
