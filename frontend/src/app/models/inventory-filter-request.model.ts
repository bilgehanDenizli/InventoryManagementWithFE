import { FilterType } from "./filter-type.enum";

export interface inventoryRequest {
    filterValue: string;
    filterType: FilterType;
}