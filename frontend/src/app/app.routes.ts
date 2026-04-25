import { Routes } from '@angular/router';
import { CachePageComponent } from './pages/cache-page/cache-page.component';
import { CategoriesPageComponent } from './pages/categories-page/categories-page.component';
import { HistoryPageComponent } from './pages/history-page/history-page.component';
import { InventoryPageComponent } from './pages/inventory-page/inventory-page.component';
import { ProductsPageComponent } from './pages/products-page/products-page.component';
import { WarehousesPageComponent } from './pages/warehouses-page/warehouses-page.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'warehouses' },
  { path: 'warehouses', component: WarehousesPageComponent },
  { path: 'products', component: ProductsPageComponent },
  { path: 'categories', component: CategoriesPageComponent },
  { path: 'inventory', component: InventoryPageComponent },
  { path: 'history', component: HistoryPageComponent },
  { path: 'cache', component: CachePageComponent },
  { path: '**', redirectTo: 'warehouses' }
];
