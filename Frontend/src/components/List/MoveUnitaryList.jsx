import React, { useState, useEffect } from "react";
import { useParams } from 'react-router-dom'; 

import kardexService from '../../services/kardex.services'; 


import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { styled } from '@mui/material/styles';
import MuiTableCell from '@mui/material/TableCell';
import Pagination from '@mui/material/Pagination';
import Stack from '@mui/material/Stack';
import { Box, Typography, Button } from '@mui/material';


const StyledTableHead = styled(TableHead)(({ theme }) => ({
  backgroundColor: '#4E7D10', 
}));

const StyledHeaderCell = styled(MuiTableCell)(({ theme }) => ({
  fontWeight: 'bold',
  color: '#ffffffff', 
  textAlign: 'center',
}));

const StyledBodyRow = styled(TableRow)(({ theme }) => ({
  '&:hover': {
    backgroundColor: theme.palette.action.hover, 
  },
  '&:last-child td, &:last-child th': {
    border: 0,
  },
}));

const MoveUnitaryList = () => {
  
  const { id } = useParams(); 
  console.log('ID RECIBIDO (desde la URL):', id);

  const [movement, setMovement] = useState([]);   
  
  useEffect(() => {
    const fetchMovements = async () => {
      console.log("-> 1. [fetchTool] Iniciando búsqueda para ID:", id);
      try {
        
        const response = await kardexService.getAllMovementsOfTool(id); 
        console.log("-> 2. [fetchTool] Respuesta COMPLETA de la API:", response); 
        console.log("-> 3. [fetchTool] Datos en response.data:", response.data); 
        setMovement(response.data || []);
      } catch (error) {
        console.error("Error al obtener movimientos", error);
        console.error("-> X. [fetchTool] ¡ERROR! Falló la llamada a la API:", error); 
        setMovement([]);
      }
    };

    if (id) {
      fetchMovements();
    }
  }, [id]);

  /*--------------------------Paginación-------------------- */
  const ITEM_PER_PAGE = 10;
  const [currentPage, setCurrentPage] = useState(1);
  const handlePageChange = (_event, value) => setCurrentPage(value);

  const totalItem = Math.max(1, Math.ceil(movement.length / ITEM_PER_PAGE));
  const indexOfLastItem = currentPage * ITEM_PER_PAGE;
  const indexOfFirstItem = indexOfLastItem - ITEM_PER_PAGE;
  const itemsCurrentPage = movement.slice(indexOfFirstItem, indexOfLastItem);

  
  return (
    <Box sx={{ padding: '16px' }}>
        <Typography variant="h5" component="h1" sx={{ mb: 2, fontWeight: 'bold' }}>
           Movimientos <br></br>
        </Typography>
      <div className="table-container">
        <TableContainer 
        component={Paper}
        sx={{ 
          borderRadius: '10px', 
          overflow: 'hidden'  
        }}>
        <Table sx={{ minWidth: 800}} aria-label="simple table">
          <StyledTableHead>
            <TableRow>
              <StyledHeaderCell key= "head-id" align="center">ID Mov.</StyledHeaderCell>
              <StyledHeaderCell key= "head-type" align="center">Tipo</StyledHeaderCell>
              <StyledHeaderCell key= "head-date" align="center"> Fecha</StyledHeaderCell>
              <StyledHeaderCell key= "head-loan" align="center"> Prestamo</StyledHeaderCell>
              <StyledHeaderCell key= "head-user" align="center"> usuario</StyledHeaderCell>
            </TableRow>
          </StyledTableHead>
          <TableBody>
            {itemsCurrentPage.map((move) => (
              <StyledBodyRow
                key={move.idKardex} 
              >
                <MuiTableCell align="center">{move.idKardex}</MuiTableCell>
                <MuiTableCell align="center">{move.typeMove}</MuiTableCell>
                <MuiTableCell align="center">{move.date}</MuiTableCell>
                <MuiTableCell align="center">{move.loan?.idLoan || 'Sin prestamo asociado'}</MuiTableCell>
                <MuiTableCell align="center">{move.user.username || 'usuario sin nombre'} </MuiTableCell>
              </StyledBodyRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

        <div className="pagination-container" style={{ marginTop: '20px', display: 'flex', justifyContent: 'center' }}>
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

      </div>
    </Box>
  );
}

export default MoveUnitaryList;