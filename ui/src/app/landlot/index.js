

import uiRouter from 'angular-ui-router';
import 'highcharts-ng/dist/highcharts-ng.js';
import thingsboardGrid from '../components/grid.directive';
import thingsboardApiUser from '../api/user.service';
import thingsboardApiLandlot from '../api/landlot.service';
import thingsboardApiCustomer from '../api/customer.service';

import LandlotRoutes from './landlot.routes';
import {LandlotController, LandlotCardController} from './landlot.controller';
import AssignLandlotToCustomerController from './assign-to-customer.controller';
import AddLandlotsToCustomerController from './add-landlots-to-customer.controller';
import LandlotDirective from './landlot.directive';

export default angular.module('thingsboard.landlot', [
    "highcharts-ng",
    uiRouter,
    thingsboardGrid,
    thingsboardApiUser,
    thingsboardApiLandlot,
    thingsboardApiCustomer
])
    .config(LandlotRoutes)
    .controller('LandlotController', LandlotController)
    .controller('LandlotCardController', LandlotCardController)
    .controller('AssignLandlotToCustomerController', AssignLandlotToCustomerController)
    .controller('AddLandlotsToCustomerController', AddLandlotsToCustomerController)
    .directive('tbLandlot', LandlotDirective)
    .name;

