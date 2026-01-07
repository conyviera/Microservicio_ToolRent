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
    cursor: 'pointer',
  },

  '&:last-child td, &:last-child th': {
    border: 0,
  },
}));


const KardexList = ({ kardex }) => {

  const formatDateTime = (isoString) => {
        const d = new Date(isoString);
        const fecha = d.toLocaleDateString('es-CL', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
        const hora = d.toLocaleTimeString('es-CL', {
            hour: '2-digit',
            minute: '2-digit',
        });
        return `${hora} ${fecha}`;
    };
  
  return (
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
            <StyledHeaderCell align="center">ID</StyledHeaderCell>
            <StyledHeaderCell align="center">Fecha</StyledHeaderCell>
            <StyledHeaderCell align="center">Tipo de movimiento</StyledHeaderCell>
            <StyledHeaderCell align="center">Cantidad</StyledHeaderCell>
            <StyledHeaderCell align="center">Nombre herramientas</StyledHeaderCell>
            <StyledHeaderCell align="center">ID prestamo</StyledHeaderCell>
            <StyledHeaderCell align="center">Usuario</StyledHeaderCell>
          </TableRow>
        </StyledTableHead>
        <TableBody>
          {kardex.map((move) => (
            <StyledBodyRow
              key={move.idKardex} 
            >
              <MuiTableCell align="center">{move.idKardex}</MuiTableCell>
              <MuiTableCell align="center">{formatDateTime(move.date)}</MuiTableCell>
              <MuiTableCell align="center">{move.typeMove}</MuiTableCell>
              <MuiTableCell align="center">{move.quantity}</MuiTableCell>
              <MuiTableCell align="center">
                {(Array.from( 
                    new Set(    
                      (move.kardexDetail || []) 
                        .map(h => h?.tool?.typeTool?.name) 
                        .filter(Boolean)                   
                    )
                  ).join(', ') 
                ) 
                || 'Sin herramientas' 
                }
              </MuiTableCell>
              <MuiTableCell align="center">{move.loan?.idLoan || 'Sin prestamo asociado'}</MuiTableCell>
              <MuiTableCell align="center">{move.user.username || 'usuario sin nombre'}</MuiTableCell>
            </StyledBodyRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
    </div>
  );
}
export default KardexList;
