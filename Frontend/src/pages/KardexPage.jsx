import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Pagination } from '@mui/material';
import Stack from '@mui/material/Stack';
import {Box,Grid,TextField,FormControl,InputLabel,Select, MenuItem,Button,Typography} from '@mui/material';
import CreditScoreIcon from '@mui/icons-material/CreditScore';
import SearchIcon from '@mui/icons-material/Search';
import ClearIcon from '@mui/icons-material/Clear';

import BasicModal from '../components/Modal.jsx';
import kardexServices from '../services/kardex.services.js'
import KardexList from '../components/List/KardexList.jsx'

function KardexPage() {

    const [open, setOpen] = useState(false);
    const [kardex, setKardex]= useState([]);

    const [dateRange, setDateRange] = useState({
        start: '',
        end: ''
    });

    const fetchKardex = async() => {
        try{
            const response= await kardexServices.getAllMove();
            setKardex(response.data || []);
            setCurrentPage(1); 
        } catch(error){
            console.error("Error al obtener los movimientos", error);
            setKardex([]);
        }
    };

    useEffect(() => { fetchKardex(); }, []);

    const handleDateChange = (e) => {
        const { name, value } = e.target;
        setDateRange({ ...dateRange, [name]: value });
    };


    const handleFilter = async () => {
        if (!dateRange.start || !dateRange.end) {
            alert("Por favor selecciona ambas fechas");
            return;
        }

        try {
            const response = await kardexServices.getAllKardexByDate(dateRange);
            setKardex(response.data || []);
            setCurrentPage(1); 
        } catch (error) {
            console.error("Error al filtrar movimientos", error);
            alert("Hubo un error al filtrar por fechas.");
        }
    };

    
    const handleClear = () => {
        setDateRange({ start: '', end: '' });
        fetchKardex();
    };


    /*--------------- Paginación---------------------- */

    const ITEM_PER_PAGE = 10;
    const [currentPage, setCurrentPage]=useState(1);
    const handlePageChange = (_event, value) => setCurrentPage(value);

    const totalItem= Math.max(1, Math.ceil(kardex.length/ITEM_PER_PAGE));
    const indexOfLastItem= currentPage * ITEM_PER_PAGE;
    const indexOfFirstItem= indexOfLastItem - ITEM_PER_PAGE;
    const itemsCurrentPage = kardex.slice(indexOfFirstItem, indexOfLastItem);


    return (
        <Box sx={{ padding: { xs: 2, md: 3 } }}>
            
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h5" component="h1" sx={{ mb: 2, fontWeight: 'bold' }}>
                    Movimientos 
                </Typography>
            </Box>

            <Box 
                sx={{ 
                    display: 'flex', 
                    gap: 2, 
                    mb: 3, 
                    backgroundColor: '#ffffffff', 
                    padding: 2, 
                    borderRadius: 2,
                    alignItems: 'center',
                    flexWrap: 'wrap' 
                }}
            >
                <TextField
                    label="Desde"
                    type="date"
                    name="start"
                    value={dateRange.start}
                    onChange={handleDateChange}
                    size="small"
                    InputLabelProps={{ shrink: true }}
                    sx={{ backgroundColor: 'white' }}
                />
                
                <TextField
                    label="Hasta"
                    type="date"
                    name="end"
                    value={dateRange.end}
                    onChange={handleDateChange}
                    size="small"
                    InputLabelProps={{ shrink: true }}
                    sx={{ backgroundColor: 'white' }}
                />

                <Button 
                    variant="contained" 
                    startIcon={<SearchIcon />} 
                    onClick={handleFilter}
                    sx={{ 
                        backgroundColor: '#4E7D10', 
                        color: 'white',             
                        textTransform: 'none',
                        '&:hover': {
                            backgroundColor: '#516a2fff', 
                            boxShadow: 'none',          
                        }
                    }}
                >
                    Filtrar
                </Button>

                <Button 
                    variant="outlined" 
                    startIcon={<ClearIcon />}
                    onClick={handleClear}
                    sx={{ 
                        backgroundColor: '#4E7D10', 
                        color: 'white',             
                        textTransform: 'none',
                        '&:hover': {
                            backgroundColor: '#516a2fff', 
                            boxShadow: 'none',          
                        }
                    }}
                >
                    Limpiar
                </Button>
            </Box>

            <div className='table-container'>
                <KardexList kardex={itemsCurrentPage ?? []} />
            </div>

            <div className="pagination-container">
                <Stack spacing={2} sx={{ mt: 2, alignItems: 'center' }}>
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
export default KardexPage;