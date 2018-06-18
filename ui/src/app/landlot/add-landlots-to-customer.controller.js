/*@ngInject*/
export default function AddLandlotsToCustomerController(landlotService, $mdDialog, $q, customerId, landlots) {

    var vm = this;

    vm.landlots = landlots;
    vm.searchText = '';

    vm.assign = assign;
    vm.cancel = cancel;
    vm.hasData = hasData;
    vm.noData = noData;
    vm.searchLandlotTextUpdated = searchLandlotTextUpdated;
    vm.toggleLandlotSelection = toggleLandlotSelection;

    vm.theLandlots = {
        getItemAtIndex: function (index) {
            if (index > vm.landlots.data.length) {
                vm.theLandlots.fetchMoreItems_(index);
                return null;
            }
            var item = vm.landlots.data[index];
            if (item) {
                item.indexNumber = index + 1;
            }
            return item;
        },

        getLength: function () {
            if (vm.landlots.hasNext) {
                return vm.landlots.data.length + vm.landlots.nextPageLink.limit;
            } else {
                return vm.landlots.data.length;
            }
        },

        fetchMoreItems_: function () {
            if (vm.landlots.hasNext && !vm.landlots.pending) {
                vm.landlots.pending = true;
                landlotService.getTenantLandlots(vm.landlots.nextPageLink, false).then(
                    function success(landlots) {
                        vm.landlots.data = vm.landlots.data.concat(landlots.data);
                        vm.landlots.nextPageLink = landlots.nextPageLink;
                        vm.landlots.hasNext = landlots.hasNext;
                        if (vm.landlots.hasNext) {
                            vm.landlots.nextPageLink.limit = vm.landlots.pageSize;
                        }
                        vm.landlots.pending = false;
                    },
                    function fail() {
                        vm.landlots.hasNext = false;
                        vm.landlots.pending = false;
                    });
            }
        }
    };

    function cancel () {
        $mdDialog.cancel();
    }

    function assign() {
        var tasks = [];
        for (var landlotId in vm.landlots.selections) {
            tasks.push(landlotService.assignLandlotToCustomer(customerId, landlotId));
        }
        $q.all(tasks).then(function () {
            $mdDialog.hide();
        });
    }

    function noData() {
        return vm.landlots.data.length == 0 && !vm.landlots.hasNext;
    }

    function hasData() {
        return vm.landlots.data.length > 0;
    }

    function toggleLandlotSelection($event, landlot) {
        $event.stopPropagation();
        var selected = angular.isDefined(landlot.selected) && landlot.selected;
        landlot.selected = !selected;
        if (landlot.selected) {
            vm.landlots.selections[landlot.id.id] = true;
            vm.landlots.selectedCount++;
        } else {
            delete vm.landlots.selections[landlot.id.id];
            vm.landlots.selectedCount--;
        }
    }

    function searchLandlotTextUpdated() {
        vm.landlots = {
            pageSize: vm.landlots.pageSize,
            data: [],
            nextPageLink: {
                limit: vm.landlots.pageSize,
                textSearch: vm.searchText
            },
            selections: {},
            selectedCount: 0,
            hasNext: true,
            pending: false
        };
    }

}