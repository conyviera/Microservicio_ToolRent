import httpClient from './http-common.js';

const PREFIX = '/loans-service';

//Función para crear un préstamo LISTO
const createLoan = async (loan) => {
    return httpClient.post(`${PREFIX}/loans/create`, loan);
};

//Función para devolver un préstamo LISTO
const returnLoan = async (id, data) => {
    return httpClient.put(`${PREFIX}/loans/return`, data);

};

//Función que trae un préstamo por su id LISTO
const getLoanById = (id) => {
    return httpClient.get(`${PREFIX}/loans/${id}`);
};

//Función que trae todos los préstamos LISTO
const getAllLoans = async () => {
  return httpClient.get(`${PREFIX}/loans/getAllLoans`);
}

//FALTA RENTAL AMOUNT
const rentalAmount = async (data) => {
    return await httpClient.post(`${PREFIX}/loans/RentalAmount`, data); 
}



export default {
    createLoan,
    returnLoan,
    getLoanById,
    getAllLoans,
    rentalAmount
};

