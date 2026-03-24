# Casos de Uso – SportReserve / Pistalia

## 1. Actores del Sistema

- **Deportista:** Usuario registrado que realiza reservas.
- **Administrador:** Gestiona pistas, empleados y supervisa el sistema.
- **Usuario no registrado:** Consulta disponibilidad y puede registrarse.

---

## 2. Lista de Casos de Uso

### CU1 – Registro de usuario
- **Actor:** Usuario no registrado  
- **Descripción:** Permite crear una cuenta en el sistema.  
- **Resultado esperado:** Usuario registrado correctamente.

---

### CU2 – Inicio de sesión
- **Actor:** Deportista  
- **Descripción:** Permite acceder a la cuenta personal.  
- **Resultado esperado:** Usuario autenticado.

---

### CU3 – Consultar disponibilidad de pistas
- **Actor:** Usuario  
- **Descripción:** Permite visualizar las pistas disponibles en una fecha determinada.  
- **Resultado esperado:** Lista de pistas disponibles.

---

### CU4 – Reservar pista
- **Actor:** Deportista  
- **Descripción:** Permite seleccionar una pista, fecha y hora para realizar una reserva.  
- **Resultado esperado:** Reserva confirmada y registrada en el sistema.

---

### CU5 – Gestionar el pago
- **Actor:** Deportista  
- **Descripción:** Permite realizar el pago de una reserva.  
- **Resultado esperado:** Pago registrado correctamente.

---

### CU6 – Cancelar reserva
- **Actor:** Deportista  
- **Descripción:** Permite cancelar una reserva existente (mínimo 24h antes).  
- **Resultado esperado:** Reserva eliminada y disponibilidad actualizada.

---

### CU7 – Gestionar pistas
- **Actor:** Administrador  
- **Descripción:** Permite crear, modificar o eliminar pistas deportivas.  
- **Resultado esperado:** Pistas actualizadas en el sistema.

---

### CU8 – Asignar empleados
- **Actor:** Administrador  
- **Descripción:** Permite asignar empleados a las pistas.  
- **Resultado esperado:** Personal asignado correctamente.

---

### CU9 – Alquiler de material
- **Actor:** Deportista  
- **Descripción:** Permite alquilar material deportivo junto con la reserva.  
- **Resultado esperado:** Material reservado correctamente.
