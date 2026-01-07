import httpClient from './http-common.js'; 

const PREFIX = '/kardexandmovements-service';

// He unificado las rutas. Si tu backend usa "/movements", cambia "kardex" por "movements" aquí abajo.
// Pero por estandar suelo recomendar que coincida con el nombre.

const getAllMove = async() => {
    // Intenta con /movements primero si estás seguro que así lo pusiste en Java
    return httpClient.get(`${PREFIX}/movements/getAllMove`)
}

const getAllMovementsOfTool = async(id) =>{
    return httpClient.get(`${PREFIX}/movements/tool/${id}`)
}

const getAllKardexByDate = async(dateRange) => {
    // Aquí usamos la ruta que mencionaste explícitamente "/range"
    // OJO: Verifica si en Java es "/movements/range" o solo "/range"
    return httpClient.get(`${PREFIX}/movements/range`, { params: dateRange });
}

export default{
    getAllMove,
    getAllMovementsOfTool,
    getAllKardexByDate
}