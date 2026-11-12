import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    title: 'AWRA — Reflect the night',
    loadComponent: () =>
      import('./features/home/home-page/home-page').then((module) => module.HomePage),
  },
  {
    path: 'producto/:id',
    title: 'Producto — AWRA',
    loadComponent: () =>
      import('./features/producto/product-page/product-page').then((module) => module.ProductPage),
  },
  {
    // El panel se carga como hijo diferido: quien visita la tienda no
    // descarga ni el servicio de administración ni las pantallas del panel.
    path: 'admin',
    loadChildren: () =>
      import('./features/admin/admin.routes').then((module) => module.adminRoutes),
  },
  {
    path: '**',
    redirectTo: '',
  },
];
