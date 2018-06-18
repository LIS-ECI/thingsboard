
import landlotsTemplate from './landlots.tpl.html';

/* eslint-enable import/no-unresolved, import/default */

/*@ngInject*/
export default function LandlotRoutes($stateProvider, types) {
    $stateProvider
        .state('home.landlots', {
            url: '/landlots',
            params: {'topIndex': 0},
            module: 'private',
            auth: ['TENANT_ADMIN', 'CUSTOMER_USER'],
            views: {
                "content@home": {
                    templateUrl: landlotsTemplate,
                    controller: 'LandlotController',
                    controllerAs: 'vm'
                }
            },
            data: {
                landlotsType: 'tenant',
                searchEnabled: true,
                searchByEntitySubtype: true,
                searchEntityType: types.entityType.landlot,
                pageTitle: 'landlot.landlots'
            },
            ncyBreadcrumb: {
                label: '{"icon": "domain", "label": "landlot.landlots"}'
            }
        })
        .state('home.customers.landlots', {
            url: '/:customerId/landlots',
            params: {'topIndex': 0},
            module: 'private',
            auth: ['TENANT_ADMIN'],
            views: {
                "content@home": {
                    templateUrl: landlotsTemplate,
                    controllerAs: 'vm',
                    controller: 'LandlotController'
                }
            },
            data: {
                landlotsType: 'customer',
                searchEnabled: true,
                searchByEntitySubtype: true,
                searchEntityType: types.entityType.landlot,
                pageTitle: 'customer.landlots'
            },
            ncyBreadcrumb: {
                label: '{"icon": "domain", "label": "{{ vm.customerLandlotsTitle }}", "translate": "false"}'
            }
        });

}