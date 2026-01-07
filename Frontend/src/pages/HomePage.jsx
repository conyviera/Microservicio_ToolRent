
import React, { useEffect, useState } from 'react'; 
import { Link } from 'react-router-dom';
import logo from '../image/logo.png';
import loanService from '../services/loan.services'; 
import { IconButton } from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import reportService from '../services/report.services';

import {
  Box,
  Grid,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
  // ----------------------------------------------------
} from '@mui/material';

const HomePage = () => {
  const [topTools, setTopTools] = useState([]);
  useEffect(() => {
    const fetchTopTools = async () => {
      try {
        const response = await reportService.getTopTools();
        setTopTools(response.data || []);
      } catch (error) {
        console.error("Error cargando reporte de herramientas", error);
      }
    };
    fetchTopTools();
  }, []);
  return (
    <Box sx={{ padding: 3 }}> 

      <Box sx={{ maxWidth: 800, margin: 'auto' }}>
        <Typography variant="h4" component="h2" gutterBottom align="center" sx={{ color: '#1976d2', fontWeight: 'bold' }}>
          🏆 Herramientas Más Populares
        </Typography>
        
        <TableContainer component={Paper} elevation={3}>
          <Table sx={{ minWidth: 300 }} aria-label="tabla de reporte">
            <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
              <TableRow>
                <TableCell><strong>Ranking</strong></TableCell>
                <TableCell><strong>Tipo de Herramienta</strong></TableCell>
                <TableCell align="right"><strong>Veces Prestada</strong></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {topTools.length > 0 ? (
                topTools.map((row, index) => (
                  <TableRow key={index} hover>
                    <TableCell component="th" scope="row">
                      #{index + 1}
                    </TableCell>
                    <TableCell>{row.toolName}</TableCell>
                    <TableCell align="right">
                      <span style={{ fontWeight: 'bold', color: index === 0 ? 'green' : 'inherit' }}>
                        {row.usageCount}
                      </span>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={3} align="center">
                    No hay datos de préstamos aún.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>

    </Box>
  );
};

export default HomePage;