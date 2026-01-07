import React, { useEffect, useState, useMemo } from 'react';
import { Pagination } from '@mui/material';
import Stack from '@mui/material/Stack';
import {
  Box,
  Grid,
  TextField,
  FormControl,
  InputLabel,
  Select, 
  MenuItem,
  Button,
  Typography
} from '@mui/material';
import CreditScoreIcon from '@mui/icons-material/CreditScore';

import BasicModal from '../components/Modal.jsx';
import LoanList from '../components/List/LoanList.jsx';
import loanServices from '../services/loan.services.js';  
import AddLoanForm from '../components/Form/AddLoanForm';
import ReturnLoanForm from '../components/Form/ReturnLoanForm';

function LoanPage() {

    const [openAdd, setOpenAdd] = useState(false);
    const [openReturn, setOpenReturn] = useState(false);
    const [loan, setLoan]= useState([]);

    const [filters, setFilters] = useState({
        idLoan: '',
        customer: '',
        state: ''
    })

    /* Cargamos los prestamos */
    const fetchLoan = async() => {
        try{
            const response= await loanServices.getAllLoans();
            setLoan(response.data || []);
        } catch(error){
            console.error("Error al obtener prestamos", error);
            setLoan([]);
        }
    };

    useEffect(() => {fetchLoan(); }, []);

    const handleLoanAdded = () => { fetchLoan(); setOpenAdd(false); }
    const handleReturnLoan = () => { fetchLoan(); setOpenReturn(false); }

    /*--------------- Paginación ---------------------- */
    const ITEM_PER_PAGE = 10;
    const [currentPage, setCurrentPage]=useState(1);
    const handlePageChange = (_event, value) => setCurrentPage(value);
    
    const handleFilterChange = (event) => {
        const { name, value } = event.target;
        setFilters(prev => ({ ...prev, [name]: value }));
        setCurrentPage(1); 
    };

    /*--------------- LÓGICA DE FILTRADO ---------------------- */
    const filteredLoans = useMemo(() => {
      const stateFilter = filters.state.toLowerCase();
      const customerFilter = filters.customer.toLowerCase();
      const idFilter = String(filters.idLoan).toLowerCase();
  
      return (loan ?? []).filter(ln => {
        const lnState = (ln.state ?? '').toString().toLowerCase();
        const lnCustomerName = (ln.customer?.name ?? '').toLowerCase(); 
        const lnId = String(ln.idLoan ?? '').toLowerCase();
  
        const matchesState = !stateFilter || lnState === stateFilter; 
        const matchesCustomer = !customerFilter || lnCustomerName.includes(customerFilter);
        const matchesId = !idFilter || lnId.includes(idFilter);
  
        return matchesState && matchesCustomer && matchesId;
      });
    }, [loan, filters]);

    /*---------------------------Paginación calculos---------------- */
    const totalItem = Math.max(1, Math.ceil(filteredLoans.length / ITEM_PER_PAGE));
    const indexOfLastItem = currentPage * ITEM_PER_PAGE;
    const indexOfFirstItem = indexOfLastItem - ITEM_PER_PAGE;
    const itemsCurrentPage = filteredLoans.slice(indexOfFirstItem, indexOfLastItem);


    return (
      <Box sx={{ padding: { xs: 2, md: 3 } }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h5" component="h1" sx={{ mb: 2, fontWeight: 'bold' }}>
          Gestión de Prestamos
        </Typography>
        <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
            <BasicModal
                open={openAdd}
                handleClose={() => setOpenAdd(false)}
                button={
                    <Button
                        variant="contained"
                        color="primary"
                        startIcon={<CreditScoreIcon />} 
                        onClick={() => setOpenAdd(true)}
                        sx={{
                            backgroundColor: '#4E7D10',
                            color: 'white',
                            mr: 2, 
                            fontWeight: 'bold',
                            '&:hover': {
                                backgroundColor: '#6b9c24',
                                filter: 'drop-shadow(0 0 10px #4E7D10)',
                            },
                        }}
                    >
                        Agregar Nuevo prestamo
                    </Button>
                }
            >
                <AddLoanForm onLoanAdded={handleLoanAdded} />
            </BasicModal>

            <BasicModal
                open={openReturn}
                handleClose={() => setOpenReturn(false)}
                button={
                    <Button
                        variant="contained"
                        color="primary"
                        
                        startIcon={<CreditScoreIcon />} 
                        onClick={() => setOpenReturn(true)}
                        
                        sx={{
                            backgroundColor: '#4E7D10',
                            color: 'white',
                            textTransform: 'capitalize',
                            fontWeight: 'bold',
                            '&:hover': {
                                backgroundColor: '#6b9c24',
                                filter: 'drop-shadow(0 0 10px #4E7D10)',
                            },
                        }}
                    >
                        Agregar Devolución
                    </Button>
                }
            >
                <ReturnLoanForm onReturnLoan={handleReturnLoan} />
            </BasicModal>
        </Box>
      </Box>

      {/* ---------------- BARRA DE FILTROS ---------------- */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2, gap: 2 }}>
              <Grid container spacing={1} sx={{ flexGrow: 1 }}>
    
                <Grid item xs={12} sm={6} md={4}>
                  <TextField
                    fullWidth
                    label="Buscar por Id"
                    variant="outlined"
                    name="idLoan"
                    value={filters.idLoan}
                    onChange={handleFilterChange}
                    size="small"
                  />
                </Grid>
      
                <Grid item xs={12} sm={6} md={4}>
                  <FormControl fullWidth size="small">
                    <InputLabel>Estado</InputLabel>
                    <Select
                        name="state"
                        value={filters.state}
                        label="Estado"
                        onChange={handleFilterChange}
                    >
                        <MenuItem value=""><em>Todos</em></MenuItem>
                        <MenuItem value="ACTIVE">Activo</MenuItem>
                        <MenuItem value="RETURNED">Devolución</MenuItem>
                        <MenuItem value="EXPIRED">Vencido</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
      
                <Grid item xs={12} sm={6} md={4}>
                  <FormControl fullWidth size="small">
                    <InputLabel>Cliente</InputLabel>
                    <Select
                      name="customer"
                      value={filters.customer}
                      label="Cliente"
                      onChange={handleFilterChange}
                    >
                      <MenuItem value=""><em>Todos</em></MenuItem>
                      {
                        Array.from(new Set(
                            (loan ?? [])
                                .map(ln => ln.customer?.name) 
                                .filter(Boolean)
                        )).map((name, index) => (
                            <MenuItem key={index} value={name}>
                              {name}
                            </MenuItem>
                          ))
                      }
                    </Select>
                  </FormControl>
                </Grid>
      
              </Grid>
            </Box>
      <div className='table-container'>
        <LoanList loans={itemsCurrentPage ?? []} />
      </div>

      <div className="pagination-container">
        <Stack spacing={2}>
          <Pagination 
            count={totalItem}         
            page={currentPage}           
            onChange={handlePageChange}   
            color="primary"
            variant="outlined" 
          />
        </Stack>
      </div>

    </Box>
    );
}
export default LoanPage;