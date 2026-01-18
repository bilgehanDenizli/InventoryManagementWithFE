import { Product } from './product.model';
import { Warehouse } from './warehouse.model';

export interface Inventory {
  inventoryId: number;
  amount: number;
  product: Product;
  warehouse: Warehouse;
  updatedAt: string;
  isDeleted: boolean;
}