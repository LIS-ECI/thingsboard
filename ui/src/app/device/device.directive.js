/*
 * Copyright © 2016-2018 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* eslint-disable import/no-unresolved, import/default */

import deviceFieldsetTemplate from './device-fieldset.tpl.html';

/* eslint-enable import/no-unresolved, import/default */

/* global google */
/*@ngInject*/
export default function DeviceDirective($compile, $templateCache, toast, $translate, types, clipboardService, parcelService, deviceService, dashboardService, farmService, alarmService, customerService, $log) {
    var linker = function (scope, element) {
        var template = $templateCache.get(deviceFieldsetTemplate);
        element.html(template);

        scope.types = types;
        scope.isAssignedToCustomer = false;
        scope.isPublic = false;
        scope.assignedCustomer = null;
        parcelService.getAllparcels().then(function(result){
            scope.parcels=result
        });

        scope.$watch('device', function(newVal) {
            if (newVal) {
                if (scope.device.customerId && scope.device.customerId.id !== types.id.nullUid) {
                    scope.isAssignedToCustomer = true;
                    customerService.getShortCustomerInfo(scope.device.customerId.id).then(
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
            }
        });

        scope.onDeviceIdCopied = function() {
            toast.showSuccess($translate.instant('device.idCopiedMessage'), 750, angular.element(element).parent().parent(), 'bottom left');
        };

        scope.copyAccessToken = function(e) {
            const trigger = e.delegateTarget || e.currentTarget;
            if (scope.device.id) {
                deviceService.getDeviceCredentials(scope.device.id.id, true).then(
                    function success(credentials) {
                        var credentialsId = credentials.credentialsId;
                        clipboardService.copyToClipboard(trigger, credentialsId).then(
                            () => {
                                toast.showSuccess($translate.instant('device.accessTokenCopiedMessage'), 750, angular.element(element).parent().parent(), 'bottom left');
                            }
                        );
                    }
                );
            }
        };

        $compile(element.contents())(scope);

        function Point(){
            this.coordinates = [];
            this.types = 'Point';
        }

        var map;

        scope.$watch('device.parcelId', function(newVal) {
            if (newVal) {
                var drawMapFarm = [];
                var drawMapParcel = [];
                for(var i = 0 ; i < scope.parcels.length ; i++){
                    if(scope.parcels[i].id.id === newVal){
                        $log.log(scope.parcels[i]);
                        if(scope.parcels[i].location !== null){
                            map = new google.maps.Map(angular.element('#mapa')[0], {
                                center: {lat: scope.parcels[i].location.coordinates[0][0][1], lng: scope.parcels[i].location.coordinates[0][0][0]},
                                zoom: 12
                            });

                            google.maps.event.addListener(map, 'click', function(clickEvent) {
                                
                                var marker = new google.maps.Marker({ map: map, position: clickEvent.latLng, draggable: true });
                                $log.log(marker);
                                var point = new Point();
                                point.coordinates[0] = clickEvent.latLng.lng();
                                point.coordinates[1] = clickEvent.latLng.lat();
                                scope.device.location = point;
                                
                            });

                            farmService.getFarm(scope.parcels[i].farmId).then(
                                function success(farm) {
                                    for(var j = 0; j < farm.location.coordinates[0].length; j++){
                                        drawMapFarm.push({lat: farm.location.coordinates[0][j][1],lng:  farm.location.coordinates[0][j][0]});
                                    }
                                    new google.maps.Polygon({
                                        paths: drawMapFarm,
                                        strokeColor: '#FF0000',
                                        strokeOpacity: 0.8,
                                        strokeWeight: 2,
                                        fillColor: '#FF0000',
                                        fillOpacity: 0.35
                                    }).setMap(map);
                                    
                                }
                            );

                            for(var k = 0; k < scope.parcels[i].location.coordinates[0].length; k++){
                                drawMapParcel.push({lat: scope.parcels[i].location.coordinates[0][k][1],lng: scope.parcels[i].location.coordinates[0][k][0]});
                            }
                            new google.maps.Polyline({
                                path: drawMapParcel,
                                geodesic: true,
                                strokeColor: '#FF0000',
                                strokeOpacity: 1.0,
                                strokeWeight: 2
                            }).setMap(map);
                            
                            dashboardService.getDevicesByParcelId(scope.parcels[i].id.id).then(
                                function success(devices) {
                                    $log.log("Estos son los devices");
                                    $log.log(devices);
                                    for(var m = 0 ; m < devices.length ; m++){
                                        if (devices[m].point !== null){
                                            verifyalarms(devices[m]);
                                        }
                                    }
                                }
                            );
                        }
                    }
                }
            }
        });

        function addmarkertomap(lat, lng, iconurl) {
            new google.maps.Marker({
                position: new google.maps.LatLng(lat, lng),
                map: map,
                icon: iconurl
            }).setMap(map);
        }
    
        function verifyalarms(devic) {
            alarmService.getHighestAlarmSeverity("DEVICE",devic.id).then(function(result3){
                if (result3 === "CRITICAL" || result3 === "MAJOR"){
                addmarkertomap(devic.point.coordinates[1],devic.point.coordinates[0],'https://maps.google.com/mapfiles/ms/icons/red-dot.png');
                }
                else if (result3 === "MINOR" || result3 === "WARNING" || result3 === "INDETERMINATE"){
                addmarkertomap(devic.point.coordinates[1],devic.point.coordinates[0],'https://maps.google.com/mapfiles/ms/icons/orange-dot.png');                                                
                }
                else{
                addmarkertomap(devic.point.coordinates[1],devic.point.coordinates[0],'https://maps.google.com/mapfiles/ms/icons/green-dot.png');   
                }                                            
            });
        }

        

    }
    return {
        restrict: "E",
        link: linker,
        scope: {
            device: '=',
            isEdit: '=',
            deviceScope: '=',
            theForm: '=',
            onAssignToCustomer: '&',
            onMakePublic: '&',
            onUnassignFromCustomer: '&',
            onManageCredentials: '&',
            onDeleteDevice: '&'
        }
    };
}
