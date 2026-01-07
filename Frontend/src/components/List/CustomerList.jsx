import React from "react";
import { Tab } from "@mui/material";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { styled } from '@mui/material/styles';
import MuiTableCell from '@mui/material/TableCell';


// Estilo para el Encabezado
const StyledTableHead = styled(TableHead)(({ theme }) => ({
  backgroundColor: '#4E7D10', // Un gris claro del tema
  
}));

// Estilo para las Celdas del Encabezado
const StyledHeaderCell = styled(MuiTableCell)(({ theme }) => ({
  fontWeight: 'bold',
  color: '#ffffffff', // Color de texto primario del tema
  textAlign: 'center',
}));

// Estilo para las Filas del Cuerpo
const StyledBodyRow = styled(TableRow)(({ theme }) => ({
  '&:hover': {
    backgroundColor: theme.palette.action.hover, // Color hover del tema
    cursor: 'pointer',
  },
  // Ocultar el borde de la última fila
  '&:last-child td, &:last-child th': {
    border: 0,
  },
}));

const CustomerList = ({ customers}) => {

  
  return (
    <>
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
              <StyledHeaderCell key= "head-id" align="center">ID Cliente</StyledHeaderCell>
              <StyledHeaderCell key= "head-name" align="center">Nombre</StyledHeaderCell>
              <StyledHeaderCell key= "head-rut" align="center">RUT</StyledHeaderCell>
              <StyledHeaderCell key= "head-phoneNumber" align="center">Teléfono</StyledHeaderCell>
              <StyledHeaderCell key= "head-email" align="center">Email</StyledHeaderCell>
              <StyledHeaderCell key= "head-state" align="center">Estado</StyledHeaderCell>
            </TableRow>
          </StyledTableHead>
          <TableBody>
            {customers.map((cust) => (
              <StyledBodyRow
                key={cust.idCustomer} 
              >
                <MuiTableCell align="center">{cust.idCustomer}</MuiTableCell>
                <MuiTableCell align="center">{cust.name}</MuiTableCell>
                <MuiTableCell align="center">{cust.rut}</MuiTableCell>
                <MuiTableCell align="center">{cust.phoneNumber}</MuiTableCell>
                <MuiTableCell align="center">{cust.email}</MuiTableCell>
                <MuiTableCell align="center">{cust.state}</MuiTableCell>
              </StyledBodyRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      </div>
    </>
  );
}
export default CustomerList;