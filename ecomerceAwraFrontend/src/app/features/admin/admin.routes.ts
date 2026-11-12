import { Routes } from '@angular/router';
import { adminGuard, guestGuard } from '../../core/auth/auth.guard';
import { AdminGateway } from '../../core/admin/ports/admin.gateway';
import { AdminHttpService } from '../../core/admin/services/admin-http.service';

/**
 * Rutas del panel de administracion.
 *
 * Van en un archivo aparte porque son lazy: el codigo del panel no se
 * descarga hasta que alguien entra en `/admin`, asi que la tienda publica
 * no paga el peso de las pantallas de gestion.
 */
export const adminRoutes: Routes = [
  {
    path: 'login',
    canActivate: [guestGuard],
    providers: [{ provide: AdminGateway, useClass: AdminHttpService }],
    loadComponent: () => import('./admin-login/admin-login').then((module) => module.AdminLogin),
  },
  {
    path: '',
    canActivate: [adminGuard],
    providers: [{ provide: AdminGateway, useClass: AdminHttpService }],
    loadComponent: () => import('./admin-layout/admin-layout').then((module) => module.AdminLayout),
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'productos',
      },
      {
        path: 'productos',
        loadComponent: () =>
          import('./product-list/product-list').then((module) => module.ProductList),
      },
      {
        // `nuevo` es el id especial que abre el formulario en alta.
        path: 'productos/:id',
        loadComponent: () =>
          import('./product-form/product-form').then((module) => module.ProductForm),
      },
      {
        path: 'inventario',
        loadComponent: () => import('./stock-page/stock-page').then((module) => module.StockPage),
      },
      {
        path: 'categorias',
        loadComponent: () =>
          import('./category-page/category-page').then((module) => module.CategoryPage),
      },
      {
        path: 'contenido',
        loadComponent: () => import('./site-page/site-page').then((module) => module.SitePage),
      },
      {
        path: 'multimedia',
        loadComponent: () => import('./media-page/media-page').then((module) => module.MediaPage),
      },
      {
        path: 'ediciones',
        loadComponent: () =>
          import('./edition-page/edition-page').then((module) => module.EditionPage),
      },
    ],
  },
];
