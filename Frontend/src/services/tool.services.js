import httpClient from './http-common.js'; 

const PREFIX = '/inventory-service';

const createLotTool = async (tool) => {
  return httpClient.post(`${PREFIX}/tools/registerBatch`, tool);
};

const getAllTypeTools = async () => {
  return httpClient.get(`${PREFIX}/tools/getAllTypeTools`);
};

const getAllcategory = async () => {
  return httpClient.get(`${PREFIX}/tools/getAllCategory`);
}

// OJO: Esta función fallará 404 siempre porque no existe en tu Java actual.
// Si la necesitas, debes crear @GetMapping("/findAllbyTypeTool/{id}") en Java.
const getAllTypeTool = async (idTypeTool) => {
  return httpClient.get( `${PREFIX}/tools/findAllTypeTool/${idTypeTool}` );
} 

const getByIdTool = async (id) => {
  return httpClient.get(`${PREFIX}/tools/${id}`); 
}

const deactivateUnusedTool = async (id)=> {
  return httpClient.patch(`${PREFIX}/tools/deactivate/${id}`)
}

const getTypeToolById = async (idTypeTool) => {
  return httpClient.get(`${PREFIX}/tools/getTypeToolById/${idTypeTool}`);
}

export default {
  createLotTool,
  getAllTypeTools,
  getAllTypeTool,
  getAllcategory,
  getTypeToolById,
  getByIdTool,
  deactivateUnusedTool
};