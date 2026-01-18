import { ProductCategory } from './product-category.model';

export interface Product {
  id: number;
  name: string;
  productCategory: ProductCategory;
}