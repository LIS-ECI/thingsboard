

export default angular.module('thingsboard.api.landlot', [])
    .factory('landlotService', LandlotService)
    .name;

/*@ngInject*/
function LandlotService($http, $q, customerService, userService, $log) {

    var service = {
        getLandlot: getLandlot,
        getLandlots: getLandlots,
        getAlllandlots: getAlllandlots,
        saveLandlot: saveLandlot,
        deleteLandlot: deleteLandlot,
        assignLandlotToCustomer: assignLandlotToCustomer,
        unassignLandlotFromCustomer: unassignLandlotFromCustomer,
        makeLandlotPublic: makeLandlotPublic,
        getTenantLandlots: getTenantLandlots,
        getCustomerLandlots: getCustomerLandlots,
        findByQuery: findByQuery,
        fetchLandlotsByNameFilter: fetchLandlotsByNameFilter,
        getLandlotTypes: getLandlotTypes,
        getHistoricalValues: getHistoricalValues,
        getImagesByLandlotId: getImagesByLandlotId,
        getFilesDates: getFilesDates
    }

    return service;

    function getLandlot(landlotId, ignoreErrors, config) {
        var deferred = $q.defer();
        var url = '/api/landlot/' + landlotId;
        if (!config) {
            config = {};
        }
        config = Object.assign(config, { ignoreErrors: ignoreErrors });
        $http.get(url, config).then(function success(response) {
            deferred.resolve(response.data);
        }, function fail() {
            deferred.reject();
        });
        return deferred.promise;
    }

    function getLandlots(landlotIds, config) {
        var deferred = $q.defer();
        var ids = '';
        for (var i=0;i<landlotIds.length;i++) {
            if (i>0) {
                ids += ',';
            }
            ids += landlotIds[i];
        }
        var url = '/api/landlots?landlotIds=' + ids;
        $http.get(url, config).then(function success(response) {
            var landlots = response.data;
            landlots.sort(function (landlot1, landlot2) {
                var id1 =  landlot1.id.id;
                var id2 =  landlot2.id.id;
                var index1 = landlotIds.indexOf(id1);
                var index2 = landlotIds.indexOf(id2);
                return index1 - index2;
            });
            deferred.resolve(landlots);
        }, function fail() {
            deferred.reject();
        });
        return deferred.promise;
    }

    function getAlllandlots(config) {
        var deferred = $q.defer();
        var landlots;
        var url = '/api/Alllandlots';
        $http.get(url,config).then(function success(response) {
            landlots=response.data;
            deferred.resolve(landlots);
        }, function fail() {
            deferred.reject();
        });
        return deferred.promise;
    }

    function getFilesDates(startDate,finishDate,config) {
        $log.log("Entró a getFilesDates");
        var deferred = $q.defer();
        var url = '/api/landlot/files/'+startDate+"/"+finishDate;
        $http.get(url,config).then(function success(response) {
            deferred.resolve(response.data);
        }, function fail() {
            deferred.reject();
        });
        return deferred.promise;
    }


    function getImagesByLandlotId(landlotId,date,config) {
        var deferred = $q.defer();
        var url = '/api/landlot/image/'+landlotId+"/"+date;
        $http.get(url,config).then(function success(response) {
            deferred.resolve(response.data);
        }, function fail() {
            deferred.reject();
        });
        $log.log(deferred.promise);
        return deferred.promise;
    }

    function saveLandlot(landlot, ignoreErrors, config) {
        $log.log(landlot);
        var deferred = $q.defer();
        var url = '/api/landlot';
        if (!config) {
            config = {};
        }
        config = Object.assign(config, { ignoreErrors: ignoreErrors });
        $http.post(url, landlot, config).then(function success(response) {
            deferred.resolve(response.data);
        }, function fail() {
            deferred.reject();
        });
        return deferred.promise;
    }

    function deleteLandlot(landlotId, ignoreErrors, config) {
        var deferred = $q.defer();
        var url = '/api/landlot/' + landlotId;
        if (!config) {
            config = {};
        }
        config = Object.assign(config, { ignoreErrors: ignoreErrors });
        $http.delete(url, config).then(function success() {
            deferred.resolve();
        }, function fail() {
            deferred.reject();
        });
        return deferred.promise;
    }

    function assignLandlotToCustomer(customerId, landlotId, ignoreErrors, config) {
        var deferred = $q.defer();
        var url = '/api/customer/' + customerId + '/landlot/' + landlotId;
        if (!config) {
            config = {};
        }
        config = Object.assign(config, { ignoreErrors: ignoreErrors });
        $http.post(url, null, config).then(function success(response) {
            deferred.resolve(response.data);
        }, function fail() {
            deferred.reject();
        });
        return deferred.promise;
    }

    function unassignLandlotFromCustomer(landlotId, ignoreErrors, config) {
        var deferred = $q.defer();
        var url = '/api/customer/landlot/' + landlotId;
        if (!config) {
            config = {};
        }
        config = Object.assign(config, { ignoreErrors: ignoreErrors });
        $http.delete(url, config).then(function success(response) {
            deferred.resolve(response.data);
        }, function fail() {
            deferred.reject();
        });
        return deferred.promise;
    }

    function makeLandlotPublic(landlotId, ignoreErrors, config) {
        var deferred = $q.defer();
        var url = '/api/customer/public/landlot/' + landlotId;
        if (!config) {
            config = {};
        }
        config = Object.assign(config, { ignoreErrors: ignoreErrors });
        $http.post(url, null, config).then(function success(response) {
            deferred.resolve(response.data);
        }, function fail() {
            deferred.reject();
        });
        return deferred.promise;
    }

    function getTenantLandlots(pageLink, applyCustomersInfo, config, type) {
        var deferred = $q.defer();
        var url = '/api/tenant/landlots?limit=' + pageLink.limit;
        if (angular.isDefined(pageLink.textSearch)) {
            url += '&textSearch=' + pageLink.textSearch;
        }
        if (angular.isDefined(pageLink.idOffset)) {
            url += '&idOffset=' + pageLink.idOffset;
        }
        if (angular.isDefined(pageLink.textOffset)) {
            url += '&textOffset=' + pageLink.textOffset;
        }
        if (angular.isDefined(type) && type.length) {
            url += '&type=' + type;
        }
        $http.get(url, config).then(function success(response) {
            if (applyCustomersInfo) {
                customerService.applyAssignedCustomersInfo(response.data.data).then(
                    function success(data) {
                        response.data.data = data;
                        deferred.resolve(response.data);
                    },
                    function fail() {
                        deferred.reject();
                    }
                );
            } else {
                deferred.resolve(response.data);
            }
        }, function fail() {
            deferred.reject();
        });
        return deferred.promise;
    }

    function getCustomerLandlots(customerId, pageLink, applyCustomersInfo, config, type) {
        var deferred = $q.defer();
        var url = '/api/customer/' + customerId + '/landlots?limit=' + pageLink.limit;
        if (angular.isDefined(pageLink.textSearch)) {
            url += '&textSearch=' + pageLink.textSearch;
        }
        if (angular.isDefined(pageLink.idOffset)) {
            url += '&idOffset=' + pageLink.idOffset;
        }
        if (angular.isDefined(pageLink.textOffset)) {
            url += '&textOffset=' + pageLink.textOffset;
        }
        if (angular.isDefined(type) && type.length) {
            url += '&type=' + type;
        }
        $http.get(url, config).then(function success(response) {
            if (applyCustomersInfo) {
                customerService.applyAssignedCustomerInfo(response.data.data, customerId).then(
                    function success(data) {
                        response.data.data = data;
                        deferred.resolve(response.data);
                    },
                    function fail() {
                        deferred.reject();
                    }
                );
            } else {
                deferred.resolve(response.data);
            }
        }, function fail() {
            deferred.reject();
        });

        return deferred.promise;
    }

    function findByQuery(query, ignoreErrors, config) {
        var deferred = $q.defer();
        var url = '/api/landlots';
        if (!config) {
            config = {};
        }
        config = Object.assign(config, { ignoreErrors: ignoreErrors });
        $http.post(url, query, config).then(function success(response) {
            deferred.resolve(response.data);
        }, function fail() {
            deferred.reject();
        });
        return deferred.promise;
    }

    function fetchLandlotsByNameFilter(landlotNameFilter, limit, applyCustomersInfo, config) {
        var deferred = $q.defer();
        var user = userService.getCurrentUser();
        var promise;
        var pageLink = {limit: limit, textSearch: landlotNameFilter};
        if (user.authority === 'CUSTOMER_USER') {
            var customerId = user.customerId;
            promise = getCustomerLandlots(customerId, pageLink, applyCustomersInfo, config);
        } else {
            promise = getTenantLandlots(pageLink, applyCustomersInfo, config);
        }
        promise.then(
            function success(result) {
                if (result.data && result.data.length > 0) {
                    deferred.resolve(result.data);
                } else {
                    deferred.resolve(null);
                }
            },
            function fail() {
                deferred.resolve(null);
            }
        );
        return deferred.promise;
    }

    function getLandlotTypes(config) {
        var deferred = $q.defer();
        var url = '/api/landlot/types';
        $http.get(url, config).then(function success(response) {
            deferred.resolve(response.data);
        }, function fail() {
            deferred.reject();
        });
        return deferred.promise;
    }

    function getHistoricalValues(landlotId, minDate, maxDate, ignoreErrors, config) {
        var deferred = $q.defer();
        var url = '/api/landlot/historical/' + landlotId+'/'+String(minDate)+'/'+String(maxDate);
        if (!config) {
            config = {};
        }
        config = Object.assign(config, { ignoreErrors: ignoreErrors });
        $http.get(url, config).then(function success(response) {
            deferred.resolve(response.data);
        }, function fail() {
            deferred.reject();
        });
        return deferred.promise;
    }

}
