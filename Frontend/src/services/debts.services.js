import httpClient from './http-common.js'; 

const PREFIX = '/loans-service';

// Función para evaluar el daño de una herramienta LISTO
const assessToolDamage = async (idDebts, data) => {
    return httpClient.post(`${PREFIX}/debts/${idDebts}/assess`, data);
};

// Función para pagar una deuda LISTO 
const payDebt = async (idDebt) => {
    return httpClient.post(`${PREFIX}/debts/${idDebt}/pay`, idDebt);
};

// Función para obtener las deudas asociadas a un préstamo LISTO
const getDebtsByLoanId = async (idLoan) => {
    return httpClient.get(`${PREFIX}/debts/loan/${idLoan}`);
};

export default {
    assessToolDamage,
    payDebt,
    getDebtsByLoanId
};