import httpClient from './http-common.js'; 

const PREFIX = '/amounts-service';

// Corrección: Se agrega "/amounts" para coincidir con @RequestMapping("/amounts")

const configurationDailyRateTypeTool = async (idTypeTool, data) => {
  return httpClient.put(`${PREFIX}/amounts/configurationDailyRateTypeTool/${idTypeTool}`, {
    dailyRate: parseInt(data)
  })
}

const configurationDebtTypeTool = async (idTypeTool, data) => {
  return httpClient.put(`${PREFIX}/amounts/setDebtRentalRate/${idTypeTool}`, {
    debtRate: parseInt(data)
  });
}

const registerReplacementTypeTool = async(idTypeTool, data) =>{
  return httpClient.put(`${PREFIX}/amounts/registerReplacementValue/${idTypeTool}`, {
    replacementValue: parseInt(data)
  });
}

export default {
  configurationDailyRateTypeTool,
  configurationDebtTypeTool,
  registerReplacementTypeTool
};