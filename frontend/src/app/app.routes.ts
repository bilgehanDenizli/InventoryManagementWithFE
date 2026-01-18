import { Routes } from '@angular/router';
import { WarehouseListComponent } from './components/warehouse-list/warehouse-list.component';

export const routes: Routes = [
    { path: '', component: WarehouseListComponent},
    { path: 'warehouses', component: WarehouseListComponent}
];
