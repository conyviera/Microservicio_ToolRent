import httpClient from './http-common.js'; 

const PREFIX = '/loans-service';

// CAMBIO: Renombramos la función a 'assessDebt' para que coincida con el formulario
const assessDebt = async (idDebts, data) => {
    return httpClient.post(`${PREFIX}/debts/${idDebts}/assess`, data);
};

const payDebt = async (idDebt) => {
    // Nota: No es necesario enviar el idDebt en el body (segundo parámetro), solo en la URL
    return httpClient.post(`${PREFIX}/debts/${idDebt}/pay`);
};

const getDebtsByLoanId = async (idLoan) => {
    return httpClient.get(`${PREFIX}/debts/loan/${idLoan}`);
};

export default {
    assessDebt, // <--- Ahora sí coincide con debtsServices.assessDebt(...)
    payDebt,
    getDebtsByLoanId
};