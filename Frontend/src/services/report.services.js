import httpClient from './http-common.js'; 

//const PREFIX = '/reports-service';

const getTopTools = async () => {
    // Corrección: Cambiado a "/reports" (plural)
    return httpClient.get(`/reports/tools-ranking`); 
};

export default {
    getTopTools
};