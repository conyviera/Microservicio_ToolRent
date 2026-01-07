import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import toolServices from '../../services/tool.services';

const DeactivateUnusedTool = ({id, onUpdate}) => {

  const [tool, setTool] = useState(null);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  
  useEffect(() => {
    const fetchTool = async () => {
      try {
        const response = await toolServices.getByIdTool(id);
        console.log("DATOS RECIBIDOS DEL BACKEND:", response.data);
        setTool(response.data);
        onUpdate();
      } catch (error) {
        console.error("Error cargando la herramienta", error);
        setMessage("Error al cargar la herramienta.");
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchTool();
    }
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault(); 

    const isConfirmed = window.confirm("¿Estás seguro que quieres dar de baja esta herramienta?");

    if (isConfirmed) {
      try {
        await toolServices.deactivateUnusedTool(id);
        
        setMessage("Éxito: La herramienta ha sido dada de baja correctamente.");
        
      } catch (error) {
        console.error("Error al dar de baja", error);
        setMessage("Error: No se pudo dar de baja la herramienta.");
      }
    }
  };

  
  if (loading) return <p>Cargando información de la herramienta...</p>;

  if (!tool) return <p>No se encontró la herramienta.</p>;

  return (
    <div className="tool-manager">
      <h2>Gestionar Herramienta: {tool.typeTool?.name || ''}</h2> 
      <p>Estado actual: <strong>{tool.state}</strong></p>

      
      {tool.state === "AVAILABLE" ? (
        <form onSubmit={handleSubmit}>
          <p>Esta herramienta está disponible para ser dada de baja.</p>
          <button type="submit" style={{ backgroundColor: 'red', color: 'white' }}>
            Dar de baja herramienta
          </button>
        </form>
      ) : (
        <div className="alert-box">
          {tool.state === "DECOMMISSIONE" && (
            <p>Esta herramienta ya ha sido dada de baja </p>
          )}
          {tool.state === "ON_LOAN" && (
            <p>La herramienta tiene un prestamo activo</p>
          )}
          {tool.state === "UNDER_REPAIR" && (
            <p>La herramienta está en mantenimiento</p>
          )}
        </div>
      )}
      {message && <p className="feedback-message">{message}</p>}
    </div>
  );
};

export default DeactivateUnusedTool;