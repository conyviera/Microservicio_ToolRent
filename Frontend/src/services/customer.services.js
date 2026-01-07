import httpClient from './http-common.js';

const PREFIX = '/customers-service';

const createCustomer = async (customer) => {
    return httpClient.post(`${PREFIX}/customers/register`, customer);
};

const getAllCustomers = async () => {
    return httpClient.get(`${PREFIX}/customers/getAll`);
};

export default {
    createCustomer, getAllCustomers
};
