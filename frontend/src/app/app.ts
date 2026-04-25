import { Component, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

interface NavItem {
  label: string;
  path: string;
}

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('inventory-admin');
  protected readonly navItems: NavItem[] = [
    { label: 'Warehouses', path: '/warehouses' },
    { label: 'Products', path: '/products' },
    { label: 'Categories', path: '/categories' },
    { label: 'Inventory', path: '/inventory' },
    { label: 'History', path: '/history' },
    { label: 'Cache', path: '/cache' }
  ];
}
