import React from "react";
import { IconButton, Tab } from "@mui/material";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { styled } from '@mui/material/styles';
import MuiTableCell from '@mui/material/TableCell';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { useNavigate} from "react-router-dom";

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


const LoanList = ({ loans }) => {

  const navigate= useNavigate();

  const handleRowClick=(id) =>{
    navigate(`/DebtList/${id}`);
  }
  
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
            <StyledHeaderCell align="center">Cliente</StyledHeaderCell>
            <StyledHeaderCell align="center">Herramientas</StyledHeaderCell>
            <StyledHeaderCell align="center">Dia de arriendo</StyledHeaderCell>
            <StyledHeaderCell align="center">Día de retorno</StyledHeaderCell>
            <StyledHeaderCell align="center">Monto</StyledHeaderCell>
            <StyledHeaderCell align="center">Estado</StyledHeaderCell>
            <StyledHeaderCell align="center">Deuda Asociada</StyledHeaderCell>
          </TableRow>
        </StyledTableHead>
        <TableBody>
          {loans.map((loan) => (
            <StyledBodyRow
              key={loan.idLoan} 
            >
              <MuiTableCell align="center">{loan.idLoan}</MuiTableCell>
              <MuiTableCell align="center">{`${loan.customer.idCustomer} - ${loan.customer.name}`}</MuiTableCell>
              <MuiTableCell align="center">{(loan.tool 
                 ?.map(h => h?.typeTool?.name) 
                 .filter(Boolean) 
                 .join(', ') 
                ) 
                || 'Sin herramientas' 
                }</MuiTableCell>
              <MuiTableCell align="center">{loan.deliveryDate}</MuiTableCell>
              <MuiTableCell align="center">{loan.returnDate}</MuiTableCell>
              <MuiTableCell align="center">{loan.rentalAmount}</MuiTableCell>
              <MuiTableCell align="center">{loan.state}</MuiTableCell>
              <MuiTableCell align="center">
                {<IconButton
                  onClick={() => handleRowClick(loan.idLoan)}
                  sx={{
                      color: '#4E7D10',
                      '&:hover': {
                        filter: 'drop-shadow(0 0 10px #4E7D10)',
                        }
                      }}
                  >
                    <ArrowForwardIcon />
                </IconButton>}
              </MuiTableCell>
            </StyledBodyRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
    </div>
  );
}
export default LoanList;
