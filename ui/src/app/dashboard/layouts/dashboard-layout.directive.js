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

import dashboardLayoutTemplate from './dashboard-layout.tpl.html';

/* eslint-enable import/no-unresolved, import/default */

/*@ngInject*/
export default function DashboardLayout() {
    return {
        restrict: "E",
        scope: true,
        bindToController: {
            layoutCtx: '=',
            dashboardCtx: '=',
            isEdit: '=',
            isMobile: '=',
            widgetEditMode: '='
        },
        controller: DashboardLayoutController,
        controllerAs: 'vm',
        templateUrl: dashboardLayoutTemplate
    };
}

/* global google */
/*@ngInject*/
function DashboardLayoutController($scope, $rootScope, $translate, $window, hotkeys, dashboardService, farmService, alarmService,deviceService, itembuffer,$log) {

    var vm = this;

    vm.noData = noData;
    vm.addWidget = addWidget;
    vm.editWidget = editWidget;
    vm.exportWidget = exportWidget;
    vm.widgetMouseDown = widgetMouseDown;
    vm.widgetClicked = widgetClicked;
    vm.prepareDashboardContextMenu = prepareDashboardContextMenu;
    vm.prepareWidgetContextMenu = prepareWidgetContextMenu;
    vm.removeWidget = removeWidget;
    vm.pasteWidget = pasteWidget;
    vm.pasteWidgetReference = pasteWidgetReference;
    vm.dashboardInited = dashboardInited;
    vm.dashboardInitFailed = dashboardInitFailed;

    vm.reload = function() {
        if (vm.dashboardContainer) {
            vm.dashboardContainer.reload();
        }
    };

    vm.setResizing = function(resizing) {
        if (vm.dashboardContainer) {
            vm.dashboardContainer.isResizing = resizing;
        }
    }

    vm.resetHighlight = function() {
        if (vm.dashboardContainer) {
            vm.dashboardContainer.resetHighlight();
        }
    };

    vm.highlightWidget = function(widget, delay) {
        if (vm.dashboardContainer) {
            vm.dashboardContainer.highlightWidget(widget, delay);
        }
    };

    vm.selectWidget = function(widget, delay) {
        if (vm.dashboardContainer) {
            vm.dashboardContainer.selectWidget(widget, delay);
        }
    };

    vm.dashboardInitComplete = false;

    initHotKeys();

    $scope.$on('$destroy', function() {
        vm.dashboardContainer = null;
    });

    $scope.$watch('vm.layoutCtx', function () {
        if (vm.layoutCtx) {
            vm.layoutCtx.ctrl = vm;
        }
    });

    var Map;
    var tempLatitude = -34.397;
    var tempLongitude = 150.644;
    var drawMapsParcels = [];
    var alarm= false;

    function showMap() {
        
        $log.log("este es el session storage");
        $log.log($window.sessionStorage.getItem('dashboardId'));

        var drawMap = [];
        dashboardService.getFarmByDashboardId($window.sessionStorage.getItem('dashboardId')).then(
            function success(farm) {
                var parcelsFarm = [];
                $log.log("esta es la finca farm:");
                $log.log(farm);

                function setPolygonFarm(){
                    if(farm.polygons.coordinates[0].length > 0){
                        tempLatitude = farm.polygons.coordinates[0][0][1];
                        tempLongitude = farm.polygons.coordinates[0][0][0];
                        for(var i = 0 ; i < farm.polygons.coordinates[0].length ; i++){
                            drawMap.push({lat: farm.polygons.coordinates[0][i][1],lng:  farm.polygons.coordinates[0][i][0]});
                        }
                        //drawMap.push({lat: farm.polygons.coordinates[0][1],lng:  farm.polygons.coordinates[0][0]});
                    }
                }

                setPolygonFarm();

                Map = new google.maps.Map(angular.element('#mapa')[0], {
                    center: {lat: tempLatitude, lng: tempLongitude},
                    zoom: 13,
                    draggable : true
                });
        
                $log.log(Map);

                function drawPolygon(){
                    $log.log("Entro a dibujar");
                    $log.log(drawMap);
                    new google.maps.Polyline({
                        path: drawMap,
                        geodesic: true,
                        strokeColor: '#FF0000',
                        strokeOpacity: 1.0,
                        strokeWeight: 2
                    }).setMap(Map);
                    drawMap = [];
                }
        
                drawPolygon();

                farmService.getParcelsByFarmId(farm.id).then(function(result){
                    parcelsFarm = result;
                    $log.log("parcelfarms");
                    $log.log(result);
                    if(parcelsFarm.length > 0){
                        for(var i = 0;i< parcelsFarm.length; i++){
                            verifyspark(parcelsFarm[i]);
                        }
                        for(var k = 0;k< parcelsFarm.length; k++){

                            dashboardService.getDevicesByParcelId(parcelsFarm[k].id).then(function(result2){
                                for(var m = 0;m< result2.length; m++){
                                    if (result2[m].point !== null){
                                        verifyalarms(result2[m]);

                                    }
                                }
                            })
                        }
                    }
                })
            },
            function fail() {
                $log.log("error");
            }
        );
    }

    showMap();

    function verifyspark(Parcel){
        $log.log("verify spark ");
        $log.log(Parcel);
        dashboardService.getSparkDevicesByParcelId(Parcel.id).then(function(sparkdevice){
        if (sparkdevice.length>0){
           for(var y = 0;y< sparkdevice.length; y++){
                if (!alarm){
                    addsparkalerts(Parcel,sparkdevice[y].id);
                }
                else{
                    break;
                }
            }
            alarm=false;

        
        }
        else{
            for(var yy = 0; yy < Parcel.polygons.coordinates[0].length;yy++){
                    drawMapsParcels.push({lat: Parcel.polygons.coordinates[0][yy][1],lng: Parcel.polygons.coordinates[0][yy][0]});
                }
                $log.log("no hay spark devices ");
                $log.log(Parcel.id);
                new google.maps.Polygon({
                paths: drawMapsParcels,
                strokeColor: '#F00000',
                strokeOpacity: 0.8,
                strokeWeight: 2,
                fillOpacity: 0.35
            }).setMap(Map);
            drawMapsParcels = [];
        }

        });
    }


   function addsparkalerts(parcel,sparkdeviceid){

        alarmService.getHighestAlarmSeverity("DEVICE",sparkdeviceid).then(function(severity){
            if (severity!==""){
                for(var h = 0; h < parcel.polygons.coordinates[0].length;h++){
                    drawMapsParcels.push({lat: parcel.polygons.coordinates[0][h][1],lng: parcel.polygons.coordinates[0][h][0]});
                }
                $log.log("entroooo alertaaaa en el parcel ");
                $log.log(parcel.id);
                new google.maps.Polygon({
                paths: drawMapsParcels,
                strokeColor: '#F00000',
                strokeOpacity: 0.8,
                strokeWeight: 2,
                fillColor: '#F00000',
                fillOpacity: 0.35
                }).setMap(Map);
                    drawMapsParcels = [];
                alarm=true;
            }

            else{
                for(var hh = 0; hh < parcel.polygons.coordinates[0].length;hh++){
                    drawMapsParcels.push({lat: parcel.polygons.coordinates[0][hh][1],lng: parcel.polygons.coordinates[0][hh][0]});
                }
                $log.log("no hay alerta ");
                $log.log(parcel.id);
                new google.maps.Polygon({
                paths: drawMapsParcels,
                strokeColor: '#F00000',
                strokeOpacity: 0.8,
                strokeWeight: 2,
                fillOpacity: 0.35
            }).setMap(Map);
            drawMapsParcels = [];
            
            }
        });

    }

    function addmarkertomap(lat, lng, iconurl,deviceId) {
       var marker = new google.maps.Marker({
            position: new google.maps.LatLng(lat, lng),
            map: Map.map,
            icon: iconurl
        });
        marker.setMap(Map);


        marker.addListener('click', function() {
            var text = "";
            deviceService.getLastTelemetryKey(deviceId).then(function(result4){
                text = result4[0]+': '+result4[1];
                var infowindow = new google.maps.InfoWindow({
                    content: text
                });
                infowindow.open(Map, marker);
            });
        });
    }


    

    


    function verifyalarms(devic) {
        alarmService.getHighestAlarmSeverity("DEVICE",devic.id).then(function(result3){

                if (result3 === "CRITICAL" || result3 === "MAJOR"){
                    addmarkertomap(devic.point.coordinates[1],devic.point.coordinates[0],'https://maps.google.com/mapfiles/ms/icons/red-dot.png',devic.id);
                }
                else if (result3 === "MINOR" || result3 === "WARNING" || result3 === "INDETERMINATE"){
                    addmarkertomap(devic.point.coordinates[1],devic.point.coordinates[0],'https://maps.google.com/mapfiles/ms/icons/orange-dot.png',devic.id);
                }
                else{
                    addmarkertomap(devic.point.coordinates[1],devic.point.coordinates[0],'https://maps.google.com/mapfiles/ms/icons/green-dot.png',devic.id);
                }

        });
    }

    function noData() {
        return vm.dashboardInitComplete && vm.layoutCtx &&
            vm.layoutCtx.widgets && vm.layoutCtx.widgets.length == 0;
    }

    function addWidget($event) {
        if (vm.dashboardCtx.onAddWidget) {
            vm.dashboardCtx.onAddWidget($event, vm.layoutCtx);
        }
    }

    function editWidget($event, widget) {
        if (vm.dashboardCtx.onEditWidget) {
            vm.dashboardCtx.onEditWidget($event, vm.layoutCtx, widget);
        }
    }

    function exportWidget($event, widget) {
        if (vm.dashboardCtx.onExportWidget) {
            vm.dashboardCtx.onExportWidget($event, vm.layoutCtx, widget);
        }
    }

    function widgetMouseDown($event, widget) {
        if (vm.dashboardCtx.onWidgetMouseDown) {
            vm.dashboardCtx.onWidgetMouseDown($event, vm.layoutCtx, widget);
        }
    }

    function widgetClicked($event, widget) {
        if (vm.dashboardCtx.onWidgetClicked) {
            vm.dashboardCtx.onWidgetClicked($event, vm.layoutCtx, widget);
        }
    }

    function prepareDashboardContextMenu() {
        if (vm.dashboardCtx.prepareDashboardContextMenu) {
            return vm.dashboardCtx.prepareDashboardContextMenu(vm.layoutCtx);
        }
    }

    function prepareWidgetContextMenu(widget) {
        if (vm.dashboardCtx.prepareWidgetContextMenu) {
            return vm.dashboardCtx.prepareWidgetContextMenu(vm.layoutCtx, widget);
        }
    }

    function removeWidget($event, widget) {
        if (vm.dashboardCtx.onRemoveWidget) {
            vm.dashboardCtx.onRemoveWidget($event, vm.layoutCtx, widget);
        }
    }

    function dashboardInitFailed() {
        var parentScope = $window.parent.angular.element($window.frameElement).scope();
        parentScope.$emit('widgetEditModeInited');
        parentScope.$apply();
        vm.dashboardInitComplete = true;
    }

    function dashboardInited(dashboardContainer) {
        vm.dashboardContainer = dashboardContainer;
        vm.dashboardInitComplete = true;
    }

    function isHotKeyAllowed(event) {
        var target = event.target || event.srcElement;
        var scope = angular.element(target).scope();
        return scope && scope.$parent !== $rootScope;
    }

    function initHotKeys() {
        $translate(['action.copy', 'action.paste', 'action.delete']).then(function (translations) {
            hotkeys.bindTo($scope)
                .add({
                    combo: 'ctrl+c',
                    description: translations['action.copy'],
                    callback: function (event) {
                        if (isHotKeyAllowed(event) &&
                            vm.isEdit && !vm.isEditingWidget && !vm.widgetEditMode) {
                            var widget = vm.dashboardContainer.getSelectedWidget();
                            if (widget) {
                                event.preventDefault();
                                copyWidget(event, widget);
                            }
                        }
                    }
                })
                .add({
                    combo: 'ctrl+r',
                    description: translations['action.copy-reference'],
                    callback: function (event) {
                        if (isHotKeyAllowed(event) &&
                            vm.isEdit && !vm.isEditingWidget && !vm.widgetEditMode) {
                            var widget = vm.dashboardContainer.getSelectedWidget();
                            if (widget) {
                                event.preventDefault();
                                copyWidgetReference(event, widget);
                            }
                        }
                    }
                })
                .add({
                    combo: 'ctrl+v',
                    description: translations['action.paste'],
                    callback: function (event) {
                        if (isHotKeyAllowed(event) &&
                            vm.isEdit && !vm.isEditingWidget && !vm.widgetEditMode) {
                            if (itembuffer.hasWidget()) {
                                event.preventDefault();
                                pasteWidget(event);
                            }
                        }
                    }
                })
                .add({
                    combo: 'ctrl+i',
                    description: translations['action.paste-reference'],
                    callback: function (event) {
                        if (isHotKeyAllowed(event) &&
                            vm.isEdit && !vm.isEditingWidget && !vm.widgetEditMode) {
                            if (itembuffer.canPasteWidgetReference(vm.dashboardCtx.dashboard,
                                    vm.dashboardCtx.state, vm.layoutCtx.id)) {
                                event.preventDefault();
                                pasteWidgetReference(event);
                            }
                        }
                    }
                })

                .add({
                    combo: 'ctrl+x',
                    description: translations['action.delete'],
                    callback: function (event) {
                        if (isHotKeyAllowed(event) &&
                            vm.isEdit && !vm.isEditingWidget && !vm.widgetEditMode) {
                            var widget = vm.dashboardContainer.getSelectedWidget();
                            if (widget) {
                                event.preventDefault();
                                vm.dashboardCtx.onRemoveWidget(event, vm.layoutCtx, widget);
                            }
                        }
                    }
                });
        });
    }

    function copyWidget($event, widget) {
        if (vm.dashboardCtx.copyWidget) {
            vm.dashboardCtx.copyWidget($event, vm.layoutCtx, widget);
        }
    }

    function copyWidgetReference($event, widget) {
        if (vm.dashboardCtx.copyWidgetReference) {
            vm.dashboardCtx.copyWidgetReference($event, vm.layoutCtx, widget);
        }
    }

    function pasteWidget($event) {
        var pos = vm.dashboardContainer.getEventGridPosition($event);
        if (vm.dashboardCtx.pasteWidget) {
            vm.dashboardCtx.pasteWidget($event, vm.layoutCtx, pos);
        }
    }

    function pasteWidgetReference($event) {
        var pos = vm.dashboardContainer.getEventGridPosition($event);
        if (vm.dashboardCtx.pasteWidgetReference) {
            vm.dashboardCtx.pasteWidgetReference($event, vm.layoutCtx, pos);
        }
    }

}
