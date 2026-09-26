# POS Sistema Paralelo — FASE 1 Backend Laravel 10 (Hostgator)

**Estado:** Plan de Implementación  
**Plataforma:** Hostgator Shared Hosting  
**Framework:** Laravel 10 + Vue 3 (Inertia)  
**Duración Estimada:** 3-4 semanas

---

## 🚀 Inicio Rápido: Setup en Hostgator

### Paso 1: Crear Estructura Base
```bash
# 1. SSH a Hostgator
ssh usuario@hosting.hostgator.com

# 2. Crear carpeta de aplicación
mkdir -p /home/usuario/public_html/pos-app
cd /home/usuario/public_html/pos-app

# 3. Instalar Laravel via Composer
composer create-project laravel/laravel . "10.*"

# 4. Configurar .env
cp .env.example .env

# 5. Generar APP_KEY
php artisan key:generate
```

### Paso 2: Configurar Base de Datos
En cPanel, crear:
- **Database:** `usuario_pos_db`
- **User:** `usuario_pos_user`
- **Password:** Generar segura

En `.env`:
```env
APP_NAME="POS System"
APP_ENV=production
APP_DEBUG=false
APP_URL=https://pos.tudominio.com

DB_CONNECTION=mysql
DB_HOST=localhost
DB_PORT=3306
DB_DATABASE=usuario_pos_db
DB_USERNAME=usuario_pos_user
DB_PASSWORD=your_secure_password

CACHE_STORE=file
QUEUE_CONNECTION=database
SESSION_DRIVER=database
```

### Paso 3: Instalar Dependencias Frontend
```bash
npm install
npm run build
```

---

## 📁 Estructura Completa del Proyecto

```
pos-app/
├── app/
│   ├── Http/
│   │   ├── Controllers/
│   │   │   ├── AuthController.php
│   │   │   ├── SalesController.php
│   │   │   ├── ProductController.php
│   │   │   ├── ReportController.php
│   │   │   └── SyncController.php
│   │   ├── Middleware/
│   │   │   └── VerifyPosAccess.php
│   │   └── Requests/
│   │       ├── StoreSaleRequest.php
│   │       └── SyncSaleRequest.php
│   ├── Models/
│   │   ├── User.php
│   │   ├── Product.php
│   │   ├── Flavor.php
│   │   ├── Presentation.php
│   │   ├── Price.php
│   │   ├── Sale.php
│   │   └── SaleItem.php
│   ├── Services/
│   │   ├── SalesService.php
│   │   ├── OfflineSyncService.php
│   │   └── ReportService.php
│   └── Exceptions/
│       └── PosException.php
├── database/
│   ├── migrations/
│   │   ├── 2024_01_01_000000_create_users_table.php
│   │   ├── 2024_01_01_000001_create_products_table.php
│   │   ├── 2024_01_01_000002_create_flavors_table.php
│   │   ├── 2024_01_01_000003_create_presentations_table.php
│   │   ├── 2024_01_01_000004_create_prices_table.php
│   │   ├── 2024_01_01_000005_create_sales_table.php
│   │   ├── 2024_01_01_000006_create_sale_items_table.php
│   │   └── 2024_01_01_000007_create_sync_queue_table.php
│   └── seeders/
│       ├── DatabaseSeeder.php
│       ├── UserSeeder.php
│       └── ProductSeeder.php
├── resources/
│   ├── js/
│   │   ├── app.js
│   │   ├── Pages/
│   │   │   ├── Auth/
│   │   │   │   ├── Login.vue
│   │   │   │   └── Register.vue
│   │   │   ├── POS/
│   │   │   │   ├── Cashier.vue (pantalla principal)
│   │   │   │   ├── Checkout.vue
│   │   │   │   └── Receipt.vue
│   │   │   └── Reports/
│   │   │       ├── Dashboard.vue
│   │   │       └── SalesReport.vue
│   │   ├── Components/
│   │   │   ├── FlavorGrid.vue
│   │   │   ├── Cart.vue
│   │   │   ├── PaymentModal.vue
│   │   │   └── OfflineBadge.vue
│   │   └── stores/
│   │       ├── auth.js (Pinia)
│   │       ├── cart.js
│   │       ├── sync.js (offline)
│   │       └── pos.js
│   └── views/
│       └── app.blade.php
├── routes/
│   ├── api.php
│   ├── web.php
│   └── channels.php
├── config/
│   ├── pos.php (custom config)
│   ├── app.php
│   └── database.php
├── storage/
│   └── app/
│       └── pos-offline/ (SQLite para offline)
├── .env
├── .env.example
├── composer.json
├── package.json
├── vite.config.js
└── artisan
```

---

## 🗄️ Migrations (Base de Datos)

### 1. Users Table
```php
// database/migrations/2024_01_01_000000_create_users_table.php
Schema::create('users', function (Blueprint $table) {
    $table->uuid('id')->primary();
    $table->string('name');
    $table->string('email')->unique();
    $table->timestamp('email_verified_at')->nullable();
    $table->string('password');
    $table->enum('role', ['vendedor', 'admin', 'propietario'])->default('vendedor');
    $table->uuid('point_id')->nullable();
    $table->boolean('active')->default(true);
    $table->timestamp('last_login_at')->nullable();
    $table->rememberToken();
    $table->timestamps();
    $table->index('email');
    $table->index('role');
});
```

### 2. Products Table
```php
Schema::create('products', function (Blueprint $table) {
    $table->uuid('id')->primary();
    $table->string('name'); // "Nieve"
    $table->string('category'); // "Helados"
    $table->text('description')->nullable();
    $table->string('sku')->unique();
    $table->boolean('active')->default(true);
    $table->timestamps();
    $table->index('category');
});
```

### 3. Flavors Table
```php
Schema::create('flavors', function (Blueprint $table) {
    $table->uuid('id')->primary();
    $table->uuid('product_id');
    $table->string('name'); // "Vainilla", "Limón"
    $table->string('code')->unique();
    $table->boolean('active')->default(true);
    $table->timestamps();
    $table->foreign('product_id')->references('id')->on('products')->onDelete('cascade');
    $table->index('product_id');
});
```

### 4. Presentations Table
```php
Schema::create('presentations', function (Blueprint $table) {
    $table->uuid('id')->primary();
    $table->uuid('product_id');
    $table->string('name'); // "Vaso Pequeño"
    $table->integer('volume_ml');
    $table->string('code');
    $table->boolean('active')->default(true);
    $table->timestamps();
    $table->foreign('product_id')->references('id')->on('products')->onDelete('cascade');
    $table->unique(['product_id', 'code']);
});
```

### 5. Prices Table
```php
Schema::create('prices', function (Blueprint $table) {
    $table->uuid('id')->primary();
    $table->uuid('presentation_id');
    $table->uuid('point_id')->nullable(); // null = precio global
    $table->uuid('flavor_id')->nullable();
    $table->bigInteger('amount_cents'); // $40 = 4000
    $table->string('currency')->default('MXN');
    $table->timestamp('valid_from')->useCurrent();
    $table->timestamp('valid_until')->nullable();
    $table->boolean('active')->default(true);
    $table->timestamps();
    $table->foreign('presentation_id')->references('id')->on('presentations')->onDelete('cascade');
    $table->index(['presentation_id', 'point_id', 'active']);
});
```

### 6. Sales Table
```php
Schema::create('sales', function (Blueprint $table) {
    $table->uuid('id')->primary();
    $table->uuid('user_id');
    $table->uuid('point_id');
    $table->bigInteger('total_cents');
    $table->enum('payment_method', ['efectivo', 'tarjeta', 'digital']);
    $table->string('payment_reference')->nullable();
    $table->string('device_id');
    $table->boolean('synced')->default(false);
    $table->timestamp('synced_at')->nullable();
    $table->timestamps();
    $table->foreign('user_id')->references('id')->on('users')->onDelete('restrict');
    $table->index('created_at');
    $table->index('synced');
});
```

### 7. Sale Items Table
```php
Schema::create('sale_items', function (Blueprint $table) {
    $table->uuid('id')->primary();
    $table->uuid('sale_id');
    $table->uuid('product_id');
    $table->uuid('flavor_id')->nullable();
    $table->uuid('presentation_id');
    $table->integer('quantity');
    $table->bigInteger('price_cents');
    $table->bigInteger('subtotal_cents');
    $table->timestamps();
    $table->foreign('sale_id')->references('id')->on('sales')->onDelete('cascade');
    $table->foreign('product_id')->references('id')->on('products')->onDelete('restrict');
    $table->foreign('flavor_id')->references('id')->on('flavors')->onDelete('set null');
    $table->foreign('presentation_id')->references('id')->on('presentations')->onDelete('restrict');
});
```

### 8. Sync Queue Table
```php
Schema::create('sync_queue', function (Blueprint $table) {
    $table->id();
    $table->string('device_id');
    $table->string('entity_type');
    $table->string('action'); // 'create', 'update', 'delete'
    $table->json('data');
    $table->boolean('synced')->default(false);
    $table->timestamp('synced_at')->nullable();
    $table->timestamps();
    $table->index(['device_id', 'synced']);
});
```

---

## 🔌 API Routes (REST)

### routes/api.php
```php
<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\SalesController;
use App\Http\Controllers\ProductController;
use App\Http\Controllers\ReportController;
use App\Http\Controllers\SyncController;

// Auth (sin protección)
Route::post('/login', [AuthController::class, 'login']);
Route::post('/register', [AuthController::class, 'register']);

// Routes protegidas con Sanctum
Route::middleware('auth:sanctum')->group(function () {
    // Auth
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/me', [AuthController::class, 'me']);

    // Productos
    Route::get('/products', [ProductController::class, 'index']);
    Route::get('/products/{id}/flavors', [ProductController::class, 'flavors']);
    Route::get('/products/{id}/presentations', [ProductController::class, 'presentations']);
    Route::get('/prices', [ProductController::class, 'prices']);

    // Ventas
    Route::post('/sales', [SalesController::class, 'store']);
    Route::get('/sales/{id}', [SalesController::class, 'show']);
    Route::get('/my-sales', [SalesController::class, 'mySales']);

    // Sincronización Offline
    Route::post('/sync/push', [SyncController::class, 'push']);
    Route::get('/sync/pull', [SyncController::class, 'pull']);
    Route::get('/sync/queue', [SyncController::class, 'queue']);

    // Reportes
    Route::get('/reports/daily', [ReportController::class, 'daily']);
    Route::get('/reports/sales-by-flavor', [ReportController::class, 'salesByFlavor']);
    Route::get('/reports/seller-performance', [ReportController::class, 'sellerPerformance']);
});
```

---

## 🎮 Controllers Principales

### AuthController.php
```php
<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class AuthController extends Controller
{
    public function login(Request $request)
    {
        $validated = $request->validate([
            'email' => 'required|email',
            'password' => 'required|string',
        ]);

        $user = User::where('email', $validated['email'])->first();

        if (!$user || !Hash::check($validated['password'], $user->password)) {
            return response()->json(['error' => 'Invalid credentials'], 401);
        }

        $token = $user->createToken('pos-token')->plainTextToken;

        return response()->json([
            'token' => $token,
            'user' => $user->only('id', 'name', 'email', 'role'),
        ]);
    }

    public function me(Request $request)
    {
        return response()->json($request->user());
    }

    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();
        return response()->json(['message' => 'Logged out']);
    }
}
```

### SalesController.php
```php
<?php

namespace App\Http\Controllers;

use App\Models\Sale;
use App\Models\SaleItem;
use App\Services\SalesService;
use Illuminate\Http\Request;

class SalesController extends Controller
{
    public function __construct(private SalesService $salesService) {}

    public function store(Request $request)
    {
        $validated = $request->validate([
            'point_id' => 'required|uuid|exists:points,id',
            'items' => 'required|array',
            'items.*.product_id' => 'required|uuid|exists:products,id',
            'items.*.flavor_id' => 'nullable|uuid|exists:flavors,id',
            'items.*.presentation_id' => 'required|uuid|exists:presentations,id',
            'items.*.quantity' => 'required|integer|min:1',
            'items.*.price_cents' => 'required|integer',
            'payment_method' => 'required|in:efectivo,tarjeta,digital',
        ]);

        try {
            $sale = $this->salesService->createSale(
                $request->user()->id,
                $validated
            );

            return response()->json([
                'id' => $sale->id,
                'total' => $sale->total_cents,
                'items' => $sale->items,
            ], 201);
        } catch (\Exception $e) {
            return response()->json(['error' => $e->getMessage()], 422);
        }
    }

    public function mySales(Request $request)
    {
        $sales = Sale::where('user_id', $request->user()->id)
            ->with('items')
            ->latest()
            ->paginate(50);

        return response()->json($sales);
    }
}
```

### SyncController.php
```php
<?php

namespace App\Http\Controllers;

use App\Services\OfflineSyncService;
use Illuminate\Http\Request;

class SyncController extends Controller
{
    public function __construct(private OfflineSyncService $syncService) {}

    public function push(Request $request)
    {
        $validated = $request->validate([
            'device_id' => 'required|string',
            'sales' => 'array',
            'sales.*.id' => 'required|uuid',
            'sales.*.data' => 'required|array',
        ]);

        $synced = $this->syncService->syncSales(
            $validated['device_id'],
            $validated['sales'] ?? []
        );

        return response()->json([
            'synced_count' => count($synced),
            'errors' => [],
        ]);
    }

    public function pull(Request $request)
    {
        $lastSync = $request->query('last_sync');

        $data = $this->syncService->getPullData($lastSync);

        return response()->json($data);
    }
}
```

---

## 🎨 Frontend (Vue 3 + Inertia)

### Pages/POS/Cashier.vue
```vue
<template>
  <div class="pos-cashier">
    <!-- Header -->
    <div class="pos-header">
      <div class="info">
        <h2>PUNTO: Centro</h2>
        <p>VENDEDOR: {{ user.name }}</p>
      </div>
      <div class="status">
        <p class="time">{{ currentTime }}</p>
        <p :class="{ online: isOnline, offline: !isOnline }">
          {{ isOnline ? 'Conectado' : 'Offline' }}
        </p>
      </div>
    </div>

    <div class="pos-content">
      <!-- Flavors Grid -->
      <div class="flavors-panel">
        <FlavorGrid 
          :flavors="flavors"
          @select="addToCart"
        />
      </div>

      <!-- Cart -->
      <div class="cart-panel">
        <Cart 
          :items="cart.items"
          :total="cart.total"
          @remove="removeFromCart"
          @checkout="proceedToCheckout"
        />
      </div>
    </div>

    <!-- Checkout Modal -->
    <PaymentModal 
      v-if="showCheckout"
      :total="cart.total"
      @confirm="completeSale"
      @cancel="showCheckout = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { usePage } from '@inertiajs/vue3'
import FlavorGrid from '@/Components/FlavorGrid.vue'
import Cart from '@/Components/Cart.vue'
import PaymentModal from '@/Components/PaymentModal.vue'

const page = usePage()
const user = computed(() => page.props.auth.user)

const flavors = ref([])
const cart = ref({ items: [], total: 0 })
const showCheckout = ref(false)
const currentTime = ref('')
const isOnline = ref(navigator.onLine)

onMounted(() => {
  fetchFlavors()
  updateTime()
  setInterval(updateTime, 1000)
  window.addEventListener('online', () => { isOnline.value = true })
  window.addEventListener('offline', () => { isOnline.value = false })
})

const fetchFlavors = async () => {
  const response = await fetch('/api/products/nieve-id/flavors', {
    headers: { Authorization: `Bearer ${page.props.auth.token}` }
  })
  flavors.value = await response.json()
}

const addToCart = (flavor) => {
  cart.value.items.push({
    id: Date.now(),
    flavor: flavor.name,
    quantity: 1,
    price: 4000 // $40
  })
  updateTotal()
}

const removeFromCart = (index) => {
  cart.value.items.splice(index, 1)
  updateTotal()
}

const updateTotal = () => {
  cart.value.total = cart.value.items.reduce((sum, item) => sum + item.price, 0)
}

const proceedToCheckout = () => {
  showCheckout.value = true
}

const completeSale = async (paymentMethod) => {
  try {
    const response = await fetch('/api/sales', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${page.props.auth.token}`
      },
      body: JSON.stringify({
        point_id: user.value.point_id,
        items: cart.value.items,
        payment_method: paymentMethod
      })
    })

    if (response.ok) {
      cart.value = { items: [], total: 0 }
      showCheckout.value = false
      // Show success toast
    }
  } catch (error) {
    console.error('Sale error:', error)
  }
}

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('es-MX', { hour: '2-digit', minute: '2-digit' })
}

onUnmounted(() => {
  window.removeEventListener('online', () => {})
  window.removeEventListener('offline', () => {})
})
</script>

<style scoped>
.pos-cashier {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #fff;
}

.pos-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #1976d2;
  color: white;
  padding: 1rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.pos-content {
  display: flex;
  flex: 1;
  gap: 0.5rem;
}

.flavors-panel {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
}

.cart-panel {
  width: 300px;
  background: #fafafa;
  border-left: 1px solid #e0e0e0;
  padding: 1rem;
}

.online {
  color: #4caf50;
}

.offline {
  color: #ffeb3b;
}
</style>
```

---

## 🔄 Sincronización Offline (Pinia Store)

### stores/sync.js
```javascript
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useSyncStore = defineStore('sync', () => {
  const syncQueue = ref([])
  const lastSync = ref(null)
  const isOnline = ref(navigator.onLine)

  const addToQueue = (sale) => {
    syncQueue.value.push({
      id: Date.now(),
      data: sale,
      timestamp: new Date(),
      synced: false
    })
    saveToDB()
  }

  const saveToDB = async () => {
    const db = await openIndexedDB()
    const tx = db.transaction('sales_queue', 'readwrite')
    await tx.objectStore('sales_queue').put({ id: 'queue', data: syncQueue.value })
  }

  const sync = async () => {
    if (!isOnline.value || syncQueue.value.length === 0) return

    try {
      const response = await fetch('/api/sync/push', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sales: syncQueue.value })
      })

      if (response.ok) {
        syncQueue.value = []
        lastSync.value = new Date()
        saveToDB()
      }
    } catch (error) {
      console.error('Sync error:', error)
    }
  }

  window.addEventListener('online', () => {
    isOnline.value = true
    sync()
  })

  return { syncQueue, lastSync, isOnline, addToQueue, sync }
})
```

---

## ✅ Deployment a Hostgator

### Paso Final: .htaccess
```apache
<IfModule mod_rewrite.c>
    <IfModule mod_negotiation.c>
        Options -MultiViews
    </IfModule>

    RewriteEngine On

    # Redirect Trailing Slashes
    RewriteCond %{REQUEST_FILENAME} !-d
    RewriteCond %{REQUEST_URI} (.+)/$
    RewriteRule ^ %1 [L,R=301]

    # Handle Front Controller
    RewriteCond %{REQUEST_FILENAME} !-d
    RewriteCond %{REQUEST_FILENAME} !-f
    RewriteRule ^ index.php [L]

    # Force HTTPS
    RewriteCond %{HTTPS} off
    RewriteRule ^ https://%{HTTP_HOST}%{REQUEST_URI} [L,R=301]
</IfModule>
```

---

## 🎯 Checklist FASE 1

- [ ] Laravel 10 setup en Hostgator
- [ ] Database migrations ejecutadas
- [ ] Sanctum authentication
- [ ] API endpoints operativos
- [ ] Vue 3 + Inertia setup
- [ ] POS UI básica
- [ ] Offline sync foundation
- [ ] Testing local
- [ ] Deploy a producción

**Tiempo:** 3-4 semanas  
**Siguiente:** FASE 2 (Refinamiento UI + Reportes)

---

Documento preparado para implementación inmediata.
