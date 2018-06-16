import parcelFieldsetTemplate from './parcel-fieldset.tpl.html';
import Action from './action';

/* eslint-enable import/no-unresolved, import/default */
/*global google*/
/*@ngInject*/
export default function ParcelDirective($compile, $templateCache, toast, $translate, types, parcelService, farmService, dashboardService, customerService, $log) {
    "use strict"
    var linker = function (scope, element) {
        var template = $templateCache.get(parcelFieldsetTemplate);
        element.html(template);

        scope.types = types;
        scope.isAssignedToCustomer = false;
        scope.isPublic = false;
        scope.assignedCustomer = null;

        farmService.getAllFarms().then(function(result){
            $log.log(result[0]);
            scope.farms=result;
        });

        scope.$watch('parcel', function(newVal) {
            if (newVal) {
                if (scope.parcel.customerId && scope.parcel.customerId.id !== types.id.nullUid) {
                    scope.isAssignedToCustomer = true;
                    customerService.getShortCustomerInfo(scope.parcel.customerId.id).then(
                        function success(customer) {
                            scope.assignedCustomer = customer;
                            scope.isPublic = customer.isPublic;
                        }
                    );
                } else {
                    scope.isAssignedToCustomer = false;
                    scope.isPublic = false;
                    scope.assignedCustomer = null;
                }
                scope.highchartsNG.series.length = 0;
                scope.highchartsNG.title['text'] = '';
                scope.tabs.length = 0;
                scope.selectedIndex = 0;
            }
        });

        scope.onParcelIdCopied = function() {
            toast.showSuccess($translate.instant('parcel.idCopiedMessage'), 750, angular.element(element).parent().parent(), 'bottom left');
        };


        $compile(element.contents())(scope);

        //-----------------------------------------------Class-------------------------------------------------------------

        function Crop(){
            this.name = '';
            this.why = '';
            this.cause = '';
            this.startCrop = new Date();
            this.weekens = 0;
            this.initialConditions = '';
            this.actions = [];
            this.finish = false;
            this.state = '';
            this.practices=[];
        }

        function Area(){
            this.extension=0.0;
            this.symbol='';

        }

        function GroundFeatures(){
            this.density = '';
            this.compaction = '';
            this.inclination = '';
            this.higrologicData = '';
        }

        //-----------------------------------------------------------------------------------------------------------------

        scope.photoType = "";
        scope.changeImageType = function(){
          $log.log(scope.photoType);
        };

        
        var delta =[0.00010,0.00010,-0.00010,-0.00010]
        var polygon2 = new Polygon();
       
        var map2;
        //var panes;
        //var overlayProjection ;
        //var sw ;
        //var ne ;
        //var div;
        //var img;
            
        DebugOverlay.prototype = new google.maps.OverlayView();
function DebugOverlay(bounds, image, map) {
                this.bounds_ = bounds;
                this.image_ = image;
                this.map_ = map;
                this.div_ = null;
                this.setMap(map);
            }

DebugOverlay.prototype.onAdd = function() {

                var div = angular.element('<div style="borderStyle:none;borderWidth:0px;position:absolute"></div>');
              
                var img = angular.element('<img src=\"'+this.image_ +'\" style="width:100%;height:100%;opacity:0.5;position:absolute"/>');
           
                angular.element(div).append(img);
this.div_ = div;
                var panes = this.getPanes();
                angular.element(panes.overlayLayer).append(div);
            };

            DebugOverlay.prototype.draw = function() {
                var overlayProjection = this.getProjection();
                var sw = overlayProjection.fromLatLngToDivPixel(this.bounds_.getSouthWest());
                var ne  = overlayProjection.fromLatLngToDivPixel(this.bounds_.getNorthEast());
var div=this.div_;
                angular.element(div).css('left',sw.x + 'px');
                angular.element(div).css('top',ne.y + 'px');
                angular.element(div).css('width',(ne.x - sw.x) + 'px');
                angular.element(div).css('height', (sw.y - ne.y) + 'px');
            };

            scope.drawNewPolygon = function(){
                var isClosed = false;
                var poly = new google.maps.Polyline({ map: map2, path: [], strokeColor: "#FF0000", strokeOpacity: 1.0, strokeWeight: 2 });

                google.maps.event.addListener(map2, 'click', first);
                function first (clickEvent) {
                    var markerIndex = poly.getPath().length;
                    var isFirstMarker = markerIndex === 0;
                    var marker = new google.maps.Marker({ map: map2, position: clickEvent.latLng, draggable: true });
                    if (isFirstMarker) {
                        google.maps.event.addListener(marker, 'click', second);
                    }
                    poly.getPath().push(clickEvent.latLng);
                    if (isClosed){
                        return;
                    }
                }
                function second () {
                    var path = poly.getPath();
                    poly.setMap(null);
                    poly = new google.maps.Polygon({ map: map2, path: path, strokeColor: "#FFF000", strokeOpacity: 0.8, strokeWeight: 2, fillColor: "#FF0000", fillOpacity: 0.35 });
                    isClosed = true;
                    if (isClosed){
                        var coordinatesArray = [];
                        for(var i = 0; i<path.getArray().length;i++){
                            coordinatesArray.push([poly.getPath().getArray()[i].lng(),poly.getPath().getArray()[i].lat()]);
                            //polygon.coordinates[i] = [poly.getPath().getArray()[i].lng(),poly.getPath().getArray()[i].lat()];
                        }
                        coordinatesArray.push(coordinatesArray[0]);
                        polygon2.coordinates.push(coordinatesArray);
                        $log.log(polygon2);
                        return;
                    }
                }
            };

       


        scope.symbol = ['ha','fg'];
        scope.practices=["The field should be free of trash, papers,plastics and empty containers","Check there is no risk of water contamination","Be acquainted with the type of pests, diseases and weeds that exist, mainly in the crop area.","Check on possible contamination sources from neighboring plots.","Signpost the place where the crop will be planted with the number of the lot or name of the crop.","With the support of the technician analyze the type of soil and its depth for good growth of the roots.","Consider the slope of the field where the planting will be done.","Avoid soil erosion and compression","Install rubbish bins in strategic zones of the field and throw the rubbish in them once the working day is over","Sow at an adequate distance"];

        scope.finishCrop = function(){
            scope.parcel.crop.finish = true;
            scope.parcel.cropsHistory.push(scope.parcel.crop);
            scope.parcel.crop = new Crop();
        };

        scope.startDate = new Date();
        scope.finishDate = new Date();
        scope.fechas = [1515992400000,1516856400000];

        scope.$watch('slider', function (value) {
            if (value != null) {
                $log.log(value);
                var updatedDate = scope.startDate;
                scope.selectedDate = new Date(updatedDate.setTime(value));
                $log.log(scope.selectedDate);
                scope.getAllImage = parcelService.getImagesByParcelId(scope.parcel.id.id,value).then(function(response){
                    $log.log(response);
                    for (var ima=0; ima<response.length; ima++){
                       var neBoundtemp = new google.maps.LatLng(response[ima].coordinates[1]+delta[0], response[ima].coordinates[0]+delta[1]);
                       var swBoundtemp = new google.maps.LatLng(response[ima].coordinates[1]+delta[2], response[ima].coordinates[0]+delta[3]);

                       var boundstemp = new google.maps.LatLngBounds(swBoundtemp, neBoundtemp);
                       var srcImagetemp = 'data:image/*;base64,'+response[ima].src;
                       new DebugOverlay(boundstemp, srcImagetemp, map2);
            
                    }
                });
            }
        });



        scope.highchartsNG = {
            options: {
                chart: {
                    type: 'line',
                    events: {
                        redraw: function() {
                            $log.log('The chart is being redrawn');
                        }

                    }
                }
            },
            navigator: {
                enabled: true,
                series: {
                    data: []
                }
            },
            xAxis: {
                type: 'datetime',
                dateTimeLabelFormats: {
                    day: "%e. %b",
                    month: "%b '%y",
                    year: "%Y"
                }
            },
            title: {
                text: ''
            },
            loading: false,
            series: [{
                name: 'Average per day',
                data: [],
                type: 'line',
            }]
        };

        var selected = null;
        var dataTelemetryUpdated = [];
        scope.tabs = [];
        scope.selectedIndex = 0;

        scope.$watch('selectedIndex', function(current){
            selected = scope.tabs[current];
            if ( current + 1 ){
                scope.highchartsNG.series.length = 0;
                scope.highchartsNG.series.push({
                    name: 'Average per day',
                    data: dataTelemetryUpdated[dataTelemetryUpdated.findIndex(data => data.titleTab === selected)]['telemetry'],
                    type: 'line'
                });
                scope.highchartsNG.title['text'] = selected;
            }
            scope.$apply();
        });

        function infoTab(title,telemetryData){
            this.titleTab = title;
            this.telemetry = telemetryData;
        }

        scope.maxDate = scope.finishDate.getTime();
        scope.minDate = scope.startDate.getTime();
        scope.updateSelectedDate = function(){

            scope.selectedDate = scope.startDate;
            scope.maxDate = scope.finishDate.getTime();
            scope.minDate = scope.startDate.getTime();
            parcelService.getFilesDates(scope.minDate,scope.maxDate).then(function(response){
                $log.log("Fechas en long");
                $log.log(scope.minDate);
                $log.log(scope.maxDate);
                scope.fechas = response;
                $log.log(scope.fechas);
            });
            parcelService.getHistoricalValues(scope.parcel.id.id,scope.minDate,scope.maxDate).then(function(result){
                $log.log(result);
                scope.tabs.length = 0;
                dataTelemetryUpdated.length = 0;
                angular.forEach(result,function (value,key){
                    scope.tabs.push(key);
                    var seriesToAdd = [];
                    angular.forEach(result[key], function (value, key){
                        var point = [];
                        point.push(Number(key));
                        point.push(value);
                        seriesToAdd.push(point);
                    });
                    var tabHistoricalValues = new infoTab(key,seriesToAdd);
                    dataTelemetryUpdated.push(tabHistoricalValues);
                });
                if(dataTelemetryUpdated.length == 1){
                    $log.log(dataTelemetryUpdated[0]);
                    scope.highchartsNG.series.push({
                        name: 'Average per day',
                        data: dataTelemetryUpdated[0]['telemetry'],
                        type: 'line'
                    });
                    scope.highchartsNG.title['text'] = dataTelemetryUpdated[0]['titleTab'];
                    scope.$apply();
                }
            });
        };


        scope.exists = function (item, list) {
            return list.indexOf(item) > -1;
        };

        scope.toggle = function (item, list) {
            var idx = list.indexOf(item);
            if (idx > -1) {
                list.splice(idx, 1);
            }
            else {
                list.push(item);
            }
        };

        scope.action = '';
        scope.addActionCrop = function(){
            var newAction = new Action();
            newAction.action = scope.action;
            scope.parcel.crop.actions.push(newAction);
            scope.action = '';
        };


        scope.someCrop = function(){
            var crop = false;
            if(scope.parcel.name == null) {
                scope.parcel.crop = new Crop();
                scope.parcel.cropsHistory = [];
            }else{
                crop = true;
            }
            return crop;
        };

        var map;
        var drawMapFarm = [];
        var drawMapParcel = [];
        function Polygon() {
            this.coordinates = [];
            this.type = 'Polygon';
        }
        scope.$watch("parcel",function(newVal){
            if(scope.parcel.id != null && newVal){
                scope.cropFarm = farmService.getFarm(scope.parcel.farmId).then(function(result){
                    var polygon = new Polygon();
                    scope.tempLatitude = -34.397;
                    scope.tempLongitude = 150.644;
                    scope.cropFarm = result;
                    $log.log(scope.cropFarm);
                    if(scope.cropFarm.location.coordinates[0].length > 0){
                        scope.tempLatitude = scope.cropFarm.location.coordinates[0][0][1];
                        scope.tempLongitude = scope.cropFarm.location.coordinates[0][0][0];
                        for(var i=0; i < scope.cropFarm.location.coordinates[0].length; i++){
                            drawMapFarm.push({lat: scope.cropFarm.location.coordinates[0][i][1],lng:  scope.cropFarm.location.coordinates[0][i][0]});
                        }
                        //drawMapFarm.push({lat: scope.cropFarm.location.coordinates[0][1],lng:  scope.cropFarm.location.coordinates[0][0]});
                        if(scope.parcel.location != null){
                            for(var j = 0 ; j < scope.parcel.location.coordinates[0].length; j++){
                                drawMapParcel.push({lat: scope.parcel.location.coordinates[0][j][1],lng: scope.parcel.location.coordinates[0][j][0]});
                            }
                        }
                        $log.log(drawMapParcel);
                    }

                    


map2 = new google.maps.Map(angular.element('#mapa2')[0], {
                        center: {lat:scope.tempLatitude , lng: scope.tempLongitude},
                        zoom: 15
                    });
$log.log(scope.tempLatitude+" "+scope.tempLongitude);

                    map = new google.maps.Map(angular.element('#mapa')[0], {
                        center: {lat: scope.tempLatitude, lng: scope.tempLongitude},
                        zoom: 15
                    });




                    new google.maps.Polyline({
                        path: drawMapFarm,
                        geodesic: true,
                        strokeColor: '#FF0000',
                        strokeOpacity: 1.0,
                        strokeWeight: 2
                    }).setMap(map);
                    drawMapFarm = [];

                    if(scope.parcel.location != null){
                        new google.maps.Polygon({
                            paths: drawMapParcel,
                            strokeColor: '#FF0000',
                            strokeOpacity: 0.8,
                            strokeWeight: 2,
                            fillColor: '#FF0000',
                            fillOpacity: 0.35
                        }).setMap(map);
                        drawMapParcel=[];
                    }


                    var isClosed = false;
                    var poly = new google.maps.Polyline({ map: map, path: [], strokeColor: "#FF0000", strokeOpacity: 1.0, strokeWeight: 2 });

                    google.maps.event.addListener(map, 'click', first);
                    function first (clickEvent) {
                        var markerIndex = poly.getPath().length;
                        var isFirstMarker = markerIndex === 0;
                        var marker = new google.maps.Marker({ map: map, position: clickEvent.latLng, draggable: true });
                        if (isFirstMarker) {
                            google.maps.event.addListener(marker, 'click', second);
                        }
                        poly.getPath().push(clickEvent.latLng);
                        if (isClosed){
                            return;
                        }
                    }
                    function second () {
                        var path = poly.getPath();
                        poly.setMap(null);
                        poly = new google.maps.Polygon({ map: map, path: path, strokeColor: "#FFF000", strokeOpacity: 0.8, strokeWeight: 2, fillColor: "#FF0000", fillOpacity: 0.35 });
                        isClosed = true;
                        if (isClosed){
                            var coordinatesArray = [];
                            for(var i = 0; i<path.getArray().length;i++){
                                coordinatesArray.push([poly.getPath().getArray()[i].lng(),poly.getPath().getArray()[i].lat()]);
                                //polygon.coordinates[i] = [poly.getPath().getArray()[i].lng(),poly.getPath().getArray()[i].lat()];
                            }
                            coordinatesArray.push(coordinatesArray[0]);
                            polygon.coordinates.push(coordinatesArray);
                            scope.parcel.location = polygon;
                            return;
                        }
                    }

                });
            }
        });

        /*scope.mostrarMapaParcel = function (){
            direction();
            map = new google.maps.Map(angular.element('#mapa')[0], {
                center: {lat: scope.tempLatitude, lng: scope.tempLongitude},
                zoom: 8
            });
            dibujar();
        };
        function dibujar(){
            $log.log("Entro a dibujar");
            $log.log(drawMap);
            new google.maps.Polygon({
                paths: drawMap,
                strokeColor: '#FF0000',
                strokeOpacity: 0.8,
                strokeWeight: 2,
                fillOpacity: 0.35
            }).setMap(map);
            drawMap = [];
        }*/

        if(scope.parcel.totalArea == null){
            scope.parcel.totalArea = new Area();
        }
        if(scope.parcel.devices == null){
            scope.parcel.devices = [];
        }
        if(scope.parcel.groundFeatures == null){
            scope.parcel.groundFeatures = new GroundFeatures();
        }
        if (scope.data== null){
            scope.data={};
        }


        //------------------------------------------------------------------------
        /*scope.labels = ['1','2','3','4'];
        scope.latitudes = new Array(scope.labels.size);
        scope.longitudes = new Array(scope.labels.size);
        scope.saveEverything = function() {
            for (var i = 0; i < scope.labels.length; i++) {
                polygon.coordinates[i]=[parseFloat(scope.longitudes[i]),parseFloat(scope.latitudes[i])];
            }
            scope.parcel.location = polygon;
        };*/

        //----------------------------------------------------------------------------
    };
    return {
        restrict: "E",
        link: linker,
        scope: {
            parcel: '=',
            isEdit: '=',
            parcelScope: '=',
            theForm: '=',
            onAssignToCustomer: '&',
            onMakePublic: '&',
            onUnassignFromCustomer: '&',
            onDeleteParcel: '&'
        }
    };
}
