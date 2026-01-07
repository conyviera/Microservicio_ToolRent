import React, { useState, useEffect } from 'react';
import AddCustomerForm from '../components/Form/AddCustomerForm.jsx';
import BasicModal from '../components/Modal.jsx';
import CustomerList from '../components/List/CustomerList.jsx';
import customerService from '../services/customer.services.js';
import MyPagination from '../components/Pagination.jsx';
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
import PersonAddIcon from '@mui/icons-material/PersonAdd';

function CustomerManagementPage() {
  const [open, setOpen] = useState(false);
  const [customers, setCustomers] = useState([]);

  // --- 1. ESTADO DE FILTROS ---
  const [filters, setFilters] = useState({
    idCustomer: '',
    name: '',
    rut: '',
    state: '' 
  });

  // Lógica para cargar clientes 
  const fetchCustomers = async () => {
    try {
      const response = await customerService.getAllCustomers();
      setCustomers(response.data);
    } catch (error) {
      console.error("Error al obtener clientes:", error);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, []);

  const handleCustomerAdded = () => {
    fetchCustomers();
    setOpen(false);
  };

  /*------------------------- Paginación  ------------------------ */
  const ITEM_PER_PAGE = 10;
  const [currentPage, setCurrentPage] = useState(1);

  const handlePageChange = (event, value) => {
    setCurrentPage(value);
  };

  /*------------------------- Filtrado------------------------ */

  // --- 2. NUEVO HANDLER UNIFICADO ---
  const handleFilterChange = (event) => {
    const { name, value } = event.target;
    setFilters(prevFilters => ({
      ...prevFilters,
      [name]: value
    }));
    setCurrentPage(1);
  };

  const filteredCustomers = customers.filter((cust) => {
    const nameFilter = filters.name.toLowerCase();
    const rutFilter = filters.rut.toLowerCase();
    const idFilter = String(filters.idCustomer).toLowerCase();
    
    const stateFilter = filters.state; 

    const matchesName = !nameFilter || (cust.name || '').toLowerCase().includes(nameFilter);
    const matchesRut = !rutFilter || (cust.rut || '').toLowerCase().includes(rutFilter);
    const matchesId = !idFilter || String(cust.idCustomer || '').toLowerCase().includes(idFilter);

    const matchesState = !stateFilter || (cust.state && String(cust.state).toUpperCase() === stateFilter);

    return matchesName && matchesRut && matchesId && matchesState;
  });

  /*----------------------- Paginación ----------------------- */
  const totalItem = Math.ceil(filteredCustomers.length / ITEM_PER_PAGE);
  const indexOfLastItem = currentPage * ITEM_PER_PAGE;
  const indexOfFirstItem = indexOfLastItem - ITEM_PER_PAGE;
  const itemsCurrentPage = filteredCustomers.slice(indexOfFirstItem, indexOfLastItem);

  /*------------------------- RENDER ------------------------- */
  return (
    <Box sx={{ padding: { xs: 2, md: 3 } }}> 

      <Box sx=
      {{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h5" component="h1" sx={{ mb: 2, fontWeight: 'bold' }}>
            Gestión de Clientes
          </Typography>
        
          <BasicModal
            open={open}
            handleClose={() => setOpen(false)}
            button={
              <Button 
                variant="contained" 
                color="primary" 
                startIcon={<PersonAddIcon />}
                aria-label="Agregar Cliente"
                title="Agregar Nuevo Cliente"
                onClick={() => setOpen(true)} 
                sx={{ 
                    backgroundColor: '#4E7D10', 
                    color: 'white',
                    '&:hover': {
                        backgroundColor: '#6b9c24',
                        filter: 'drop-shadow(0 0 10px #4E7D10)', 
                    },
                    textTransform: 'capitalize',
                    fontWeight: 'bold',
                }}
              >
                Agregar cliente
              </Button>
            }
          >
              <AddCustomerForm onCustomerAdded={handleCustomerAdded} />
          </BasicModal>  
        </Box>

      {/* --- 4. NUEVA BARRA DE FILTROS Y BOTÓN "AGREGAR" --- */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2, gap: 2 }}>
        
        {/* Contenedor de Filtros */}
        <Grid container spacing={1} sx={{ flexGrow: 1 }}>
          <Grid item xs={12} sm={6} md={3}>
            <TextField
              fullWidth
              label="Buscar por Id"
              variant="outlined"
              name="idCustomer" // IMPORTANTE: el 'name' debe coincidir con la clave del estado 'filters'
              value={filters.idCustomer}
              onChange={handleFilterChange}
              size="small" // Para un look más compacto como en la imagen
            />
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <TextField
              fullWidth
              label="Buscar por Nombre"
              variant="outlined"
              name="name" // IMPORTANTE: el 'name' debe coincidir con la clave del estado 'filters'
              value={filters.name}
              onChange={handleFilterChange}
              size="small" // Para un look más compacto como en la imagen
            />
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <TextField
              fullWidth
              label="Buscar por RUT"
              variant="outlined"
              name="rut" // IMPORTANTE
              value={filters.rut}
              onChange={handleFilterChange}
              size="small"
            />
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
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
                <MenuItem value="RESTRICTED">Restringido</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>
      </Box>
      
      {/* --- FIN DE LA NUEVA BARRA --- */}


      {/* --- El resto de tu página (Tabla y Paginación) --- */}
      <div className='table-container'>
        <CustomerList customers={itemsCurrentPage} />
      </div>

      <div className="pagination-container">
        <MyPagination
          count={totalItem}
          page={currentPage}
          onChange={handlePageChange}
        />
      </div>
    </Box>
  );
}

export default CustomerManagementPage;