# Documento de Requisitos – SportReserve / Pistalia

## 1. Identificación del Problema
Actualmente existe dificultad para gestionar reservas de pistas deportivas, así como una falta de personalización en la experiencia del usuario.  
El sistema busca ofrecer una solución que permita realizar reservas de forma rápida y eficiente, además de facilitar la gestión interna del personal.

---

## 2. Usuarios del Sistema

### Deportistas
Usuarios registrados que:
- Buscan pistas
- Realizan reservas
- Gestionan pagos

### Administradores
Personal del centro que:
- Gestiona pistas
- Supervisa reservas
- Administra empleados

### Usuarios No Registrados
Usuarios que:
- Consultan disponibilidad
- Pueden registrarse en el sistema

---

## 3. Requisitos Funcionales (RF)

- **RF1:** Registro de usuarios.
- **RF2:** Inicio de sesión.
- **RF3:** Gestión de pistas (crear, editar, eliminar).
- **RF4:** Consulta de disponibilidad de pistas.
- **RF5:** Reserva de pistas en bloques de 1 hora.
- **RF6:** Cancelación de reservas con al menos 24h de antelación.
- **RF7:** Gestión de pagos (efectivo o cuenta).
- **RF8:** Recomendación de pistas según deporte favorito.
- **RF9:** Envío de recordatorios por email.
- **RF10:** Gestión de empleados asignados a pistas.

---

## 4. Requisitos No Funcionales (RNF)

- **RNF1:** La aplicación debe ser responsive.
- **RNF2:** Interfaz intuitiva y fácil de usar.
- **RNF3:** Uso de base de datos relacional.
- **RNF4:** Seguridad en autenticación de usuarios.
- **RNF5:** Tiempo de respuesta inferior a 2 segundos.
- **RNF6:** Uso de control de versiones con GitHub.

---

## 5. Estructura de una Reserva

Cada reserva debe contener la siguiente información:

- **Usuario:** ID del deportista.
- **Pista:** ID y ubicación.
- **Horario:** Fecha, hora de inicio y fin.
- **Pago:** Método y estado del pago.

---

## 6. Objetivo del Sistema

Desarrollar una aplicación web que permita gestionar de forma eficiente las reservas de instalaciones deportivas, mejorando la experiencia del usuario y facilitando la administración del centro.
