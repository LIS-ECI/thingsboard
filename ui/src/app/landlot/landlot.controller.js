
import addLandlotTemplate from './add-landlot.tpl.html';
import landlotCard from './landlot-card.tpl.html';
import assignToCustomerTemplate from './assign-to-customer.tpl.html';
import addLandlotsToCustomerTemplate from './add-landlots-to-customer.tpl.html';

/* eslint-enable import/no-unresolved, import/default */

/*@ngInject*/
export function LandlotCardController(types) {

    var vm = this;

    vm.types = types;

    vm.isAssignedToCustomer = function() {
        if (vm.item && vm.item.customerId && vm.parentCtl.landlotsScope === 'tenant' &&
            vm.item.customerId.id != vm.types.id.nullUid && !vm.item.assignedCustomer.isPublic) {
            return true;
        }
        return false;
    }

    vm.isPublic = function() {
        if (vm.item && vm.item.assignedCustomer && vm.parentCtl.landlotsScope === 'tenant' && vm.item.assignedCustomer.isPublic) {
            return true;
        }
        return false;
    }
}


/*@ngInject*/
export function LandlotController($rootScope, userService, landlotService, customerService, $state, $stateParams,
                                $document, $mdDialog, $q, $translate, types) {

    var customerId = $stateParams.customerId;

    var landlotActionsList = [];

    var landlotGroupActionsList = [];

    var vm = this;

    vm.types = types;

    vm.landlotGridConfig = {
        deleteItemTitleFunc: deleteLandlotTitle,
        deleteItemContentFunc: deleteLandlotText,
        deleteItemsTitleFunc: deleteLandlotsTitle,
        deleteItemsActionTitleFunc: deleteLandlotsActionTitle,
        deleteItemsContentFunc: deleteLandlotsText,

        saveItemFunc: saveLandlot,

        getItemTitleFunc: getLandlotTitle,

        itemCardController: 'LandlotCardController',
        itemCardTemplateUrl: landlotCard,
        parentCtl: vm,

        actionsList: landlotActionsList,
        groupActionsList: landlotGroupActionsList,

        onGridInited: gridInited,

        addItemTemplateUrl: addLandlotTemplate,

        addItemText: function() { return $translate.instant('landlot.add-landlot-text') },
        noItemsText: function() { return $translate.instant('landlot.no-landlots-text') },
        itemDetailsText: function() { return $translate.instant('landlot.landlot-details') },
        isDetailsReadOnly: isCustomerUser,
        isSelectionEnabled: function () {
            return !isCustomerUser();
        }
    };

    if (angular.isDefined($stateParams.items) && $stateParams.items !== null) {
        vm.landlotGridConfig.items = $stateParams.items;
    }

    if (angular.isDefined($stateParams.topIndex) && $stateParams.topIndex > 0) {
        vm.landlotGridConfig.topIndex = $stateParams.topIndex;
    }

    vm.landlotsScope = $state.$current.data.landlotsType;

    vm.assignToCustomer = assignToCustomer;
    vm.makePublic = makePublic;
    vm.unassignFromCustomer = unassignFromCustomer;

    initController();

    function initController() {
        var fetchLandlotsFunction = null;
        var deleteLandlotFunction = null;
        var refreshLandlotsParamsFunction = null;

        var user = userService.getCurrentUser();

        if (user.authority === 'CUSTOMER_USER') {
            vm.landlotsScope = 'customer_user';
            customerId = user.customerId;
        }
        if (customerId) {
            vm.customerLandlotsTitle = $translate.instant('customer.landlots');
            customerService.getShortCustomerInfo(customerId).then(
                function success(info) {
                    if (info.isPublic) {
                        vm.customerLandlotsTitle = $translate.instant('customer.public-landlots');
                    }
                }
            );
        }

        if (vm.landlotsScope === 'tenant') {
            fetchLandlotsFunction = function (pageLink, landlotType) {
                return landlotService.getTenantLandlots(pageLink, true, null, landlotType);
            };
            deleteLandlotFunction = function (landlotId) {
                return landlotService.deleteLandlot(landlotId);
            };
            refreshLandlotsParamsFunction = function() {
                return {"topIndex": vm.topIndex};
            };

            landlotActionsList.push({
                onAction: function ($event, item) {
                    makePublic($event, item);
                },
                name: function() { return $translate.instant('action.share') },
                details: function() { return $translate.instant('landlot.make-public') },
                icon: "share",
                isEnabled: function(landlot) {
                    return landlot && (!landlot.customerId || landlot.customerId.id === types.id.nullUid);
                }
            });

            landlotActionsList.push(
                {
                    onAction: function ($event, item) {
                        assignToCustomer($event, [ item.id.id ]);
                    },
                    name: function() { return $translate.instant('action.assign') },
                    details: function() { return $translate.instant('landlot.assign-to-customer') },
                    icon: "assignment_ind",
                    isEnabled: function(landlot) {
                        return landlot && (!landlot.customerId || landlot.customerId.id === types.id.nullUid);
                    }
                }
            );

            landlotActionsList.push(
                {
                    onAction: function ($event, item) {
                        unassignFromCustomer($event, item, false);
                    },
                    name: function() { return $translate.instant('action.unassign') },
                    details: function() { return $translate.instant('landlot.unassign-from-customer') },
                    icon: "assignment_return",
                    isEnabled: function(landlot) {
                        return landlot && landlot.customerId && landlot.customerId.id !== types.id.nullUid && !landlot.assignedCustomer.isPublic;
                    }
                }
            );

            landlotActionsList.push({
                onAction: function ($event, item) {
                    unassignFromCustomer($event, item, true);
                },
                name: function() { return $translate.instant('action.make-private') },
                details: function() { return $translate.instant('landlot.make-private') },
                icon: "reply",
                isEnabled: function(landlot) {
                    return landlot && landlot.customerId && landlot.customerId.id !== types.id.nullUid && landlot.assignedCustomer.isPublic;
                }
            });

            landlotActionsList.push(
                {
                    onAction: function ($event, item) {
                        vm.grid.deleteItem($event, item);
                    },
                    name: function() { return $translate.instant('action.delete') },
                    details: function() { return $translate.instant('landlot.delete') },
                    icon: "delete"
                }
            );

            landlotGroupActionsList.push(
                {
                    onAction: function ($event, items) {
                        assignLandlotsToCustomer($event, items);
                    },
                    name: function() { return $translate.instant('landlot.assign-landlots') },
                    details: function(selectedCount) {
                        return $translate.instant('landlot.assign-landlots-text', {count: selectedCount}, "messageformat");
                    },
                    icon: "assignment_ind"
                }
            );

            landlotGroupActionsList.push(
                {
                    onAction: function ($event) {
                        vm.grid.deleteItems($event);
                    },
                    name: function() { return $translate.instant('landlot.delete-landlots') },
                    details: deleteLandlotsActionTitle,
                    icon: "delete"
                }
            );



        } else if (vm.landlotsScope === 'customer' || vm.landlotsScope === 'customer_user') {
            fetchLandlotsFunction = function (pageLink, landlotType) {
                return landlotService.getCustomerLandlots(customerId, pageLink, true, null, landlotType);
            };
            deleteLandlotFunction = function (landlotId) {
                return landlotService.unassignLandlotFromCustomer(landlotId);
            };
            refreshLandlotsParamsFunction = function () {
                return {"customerId": customerId, "topIndex": vm.topIndex};
            };

            if (vm.landlotsScope === 'customer') {
                landlotActionsList.push(
                    {
                        onAction: function ($event, item) {
                            unassignFromCustomer($event, item, false);
                        },
                        name: function() { return $translate.instant('action.unassign') },
                        details: function() { return $translate.instant('landlot.unassign-from-customer') },
                        icon: "assignment_return",
                        isEnabled: function(landlot) {
                            return landlot && !landlot.assignedCustomer.isPublic;
                        }
                    }
                );
                landlotActionsList.push(
                    {
                        onAction: function ($event, item) {
                            unassignFromCustomer($event, item, true);
                        },
                        name: function() { return $translate.instant('action.make-private') },
                        details: function() { return $translate.instant('landlot.make-private') },
                        icon: "reply",
                        isEnabled: function(landlot) {
                            return landlot && landlot.assignedCustomer.isPublic;
                        }
                    }
                );

                landlotGroupActionsList.push(
                    {
                        onAction: function ($event, items) {
                            unassignLandlotsFromCustomer($event, items);
                        },
                        name: function() { return $translate.instant('landlot.unassign-landlots') },
                        details: function(selectedCount) {
                            return $translate.instant('landlot.unassign-landlots-action-title', {count: selectedCount}, "messageformat");
                        },
                        icon: "assignment_return"
                    }
                );

                vm.landlotGridConfig.addItemAction = {
                    onAction: function ($event) {
                        addLandlotsToCustomer($event);
                    },
                    name: function() { return $translate.instant('landlot.assign-landlots') },
                    details: function() { return $translate.instant('landlot.assign-new-landlot') },
                    icon: "add"
                };


            } else if (vm.landlotsScope === 'customer_user') {
                vm.landlotGridConfig.addItemAction = {};
            }
        }

        vm.landlotGridConfig.refreshParamsFunc = refreshLandlotsParamsFunction;
        vm.landlotGridConfig.fetchItemsFunc = fetchLandlotsFunction;
        vm.landlotGridConfig.deleteItemFunc = deleteLandlotFunction;

    }

    function deleteLandlotTitle(landlot) {
        return $translate.instant('landlot.delete-landlot-title', {landlotName: landlot.name});
    }

    function deleteLandlotText() {
        return $translate.instant('landlot.delete-landlot-text');
    }

    function deleteLandlotsTitle(selectedCount) {
        return $translate.instant('landlot.delete-landlots-title', {count: selectedCount}, 'messageformat');
    }

    function deleteLandlotsActionTitle(selectedCount) {
        return $translate.instant('landlot.delete-landlots-action-title', {count: selectedCount}, 'messageformat');
    }

    function deleteLandlotsText () {
        return $translate.instant('landlot.delete-landlots-text');
    }

    function gridInited(grid) {
        vm.grid = grid;
    }

    function getLandlotTitle(landlot) {
        return landlot ? landlot.name : '';
    }

    function saveLandlot(landlot) {
        var deferred = $q.defer();
        landlotService.saveLandlot(landlot).then(
            function success(savedLandlot) {
                $rootScope.$broadcast('landlotSaved');
                var landlots = [ savedLandlot ];
                customerService.applyAssignedCustomersInfo(landlots).then(
                    function success(items) {
                        if (items && items.length == 1) {
                            deferred.resolve(items[0]);
                        } else {
                            deferred.reject();
                        }
                    },
                    function fail() {
                        deferred.reject();
                    }
                );
            },
            function fail() {
                deferred.reject();
            }
        );
        return deferred.promise;
    }

    function isCustomerUser() {
        return vm.landlotsScope === 'customer_user';
    }

    function assignToCustomer($event, landlotIds) {
        if ($event) {
            $event.stopPropagation();
        }
        var pageSize = 10;
        customerService.getCustomers({limit: pageSize, textSearch: ''}).then(
            function success(_customers) {
                var customers = {
                    pageSize: pageSize,
                    data: _customers.data,
                    nextPageLink: _customers.nextPageLink,
                    selection: null,
                    hasNext: _customers.hasNext,
                    pending: false
                };
                if (customers.hasNext) {
                    customers.nextPageLink.limit = pageSize;
                }
                $mdDialog.show({
                    controller: 'AssignLandlotToCustomerController',
                    controllerAs: 'vm',
                    templateUrl: assignToCustomerTemplate,
                    locals: {landlotIds: landlotIds, customers: customers},
                    parent: angular.element($document[0].body),
                    fullscreen: true,
                    targetEvent: $event
                }).then(function () {
                    vm.grid.refreshList();
                }, function () {
                });
            },
            function fail() {
            });
    }

    function addLandlotsToCustomer($event) {
        if ($event) {
            $event.stopPropagation();
        }
        var pageSize = 10;
        landlotService.getTenantLandlots({limit: pageSize, textSearch: ''}, false).then(
            function success(_landlots) {
                var landlots = {
                    pageSize: pageSize,
                    data: _landlots.data,
                    nextPageLink: _landlots.nextPageLink,
                    selections: {},
                    selectedCount: 0,
                    hasNext: _landlots.hasNext,
                    pending: false
                };
                if (landlots.hasNext) {
                    landlots.nextPageLink.limit = pageSize;
                }
                $mdDialog.show({
                    controller: 'AddLandlotsToCustomerController',
                    controllerAs: 'vm',
                    templateUrl: addLandlotsToCustomerTemplate,
                    locals: {customerId: customerId, landlots: landlots},
                    parent: angular.element($document[0].body),
                    fullscreen: true,
                    targetEvent: $event
                }).then(function () {
                    vm.grid.refreshList();
                }, function () {
                });
            },
            function fail() {
            });
    }

    function assignLandlotsToCustomer($event, items) {
        var landlotIds = [];
        for (var id in items.selections) {
            landlotIds.push(id);
        }
        assignToCustomer($event, landlotIds);
    }

    function unassignFromCustomer($event, landlot, isPublic) {
        if ($event) {
            $event.stopPropagation();
        }
        var title;
        var content;
        var label;
        if (isPublic) {
            title = $translate.instant('landlot.make-private-landlot-title', {landlotName: landlot.name});
            content = $translate.instant('landlot.make-private-landlot-text');
            label = $translate.instant('landlot.make-private');
        } else {
            title = $translate.instant('landlot.unassign-landlot-title', {landlotName: landlot.name});
            content = $translate.instant('landlot.unassign-landlot-text');
            label = $translate.instant('landlot.unassign-landlot');
        }
        var confirm = $mdDialog.confirm()
            .targetEvent($event)
            .title(title)
            .htmlContent(content)
            .ariaLabel(label)
            .cancel($translate.instant('action.no'))
            .ok($translate.instant('action.yes'));
        $mdDialog.show(confirm).then(function () {
            landlotService.unassignLandlotFromCustomer(landlot.id.id).then(function success() {
                vm.grid.refreshList();
            });
        });
    }

    function unassignLandlotsFromCustomer($event, items) {
        var confirm = $mdDialog.confirm()
            .targetEvent($event)
            .title($translate.instant('landlot.unassign-landlots-title', {count: items.selectedCount}, 'messageformat'))
            .htmlContent($translate.instant('landlot.unassign-landlots-text'))
            .ariaLabel($translate.instant('landlot.unassign-landlot'))
            .cancel($translate.instant('action.no'))
            .ok($translate.instant('action.yes'));
        $mdDialog.show(confirm).then(function () {
            var tasks = [];
            for (var id in items.selections) {
                tasks.push(landlotService.unassignLandlotFromCustomer(id));
            }
            $q.all(tasks).then(function () {
                vm.grid.refreshList();
            });
        });
    }

    function makePublic($event, landlot) {
        if ($event) {
            $event.stopPropagation();
        }
        var confirm = $mdDialog.confirm()
            .targetEvent($event)
            .title($translate.instant('landlot.make-public-landlot-title', {landlotName: landlot.name}))
            .htmlContent($translate.instant('landlot.make-public-landlot-text'))
            .ariaLabel($translate.instant('landlot.make-public'))
            .cancel($translate.instant('action.no'))
            .ok($translate.instant('action.yes'));
        $mdDialog.show(confirm).then(function () {
            landlotService.makeLandlotPublic(landlot.id.id).then(function success() {
                vm.grid.refreshList();
            });
        });
    }
}
