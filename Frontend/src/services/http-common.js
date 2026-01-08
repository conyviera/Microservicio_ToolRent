import axios from "axios";
import keycloak from "./keycloak";

const baseURL = 'http://192.168.49.2:30090'; 

console.log("Conectando a API Gateway en Kubernetes:", baseURL);

const httpClient = axios.create({
  baseURL: baseURL,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json"
  }
});

// Interceptor que se ejecuta en cada petición
httpClient.interceptors.request.use(async (config) => {
  console.log("--- INTERCEPTOR DE AXIOS ACTIVADO ---");
  
  if (keycloak.authenticated) {
    console.log("Keycloak está autenticado. Intentando actualizar token...");
    try {
      await keycloak.updateToken(30);
      config.headers.Authorization = `Bearer ${keycloak.token}`;
      console.log("Token añadido a la cabecera:", `Bearer ${keycloak.token.substring(0, 20)}...`);
    } catch (error) {
      console.error("Failed to refresh token or update auth header", error);
      // El token no se pudo actualizar, continuar sin él
    }
  } else {
    console.log("Keycloak no está autenticado. Petición sin token.");
  }
  
  return config;  // ✅ SIEMPRE retornar config
  
}, (error) => {
  return Promise.reject(error);
});

// Interceptor para respuestas (debugging)
httpClient.interceptors.response.use(
  (response) => {
    console.log("✅ Respuesta exitosa:", response.status);
    return response;
  },
  (error) => {
    console.error("❌ Error en respuesta:", error.response?.status, error.message);
    return Promise.reject(error);
  }
);

export default httpClient;
