export const mockMaintenanceRules = [
  // 🛢️ CAMBIO DE ACEITE
  { ruleType: 'Cambio de aceite motor liviano', intervalKm: 5000, thresholdKm: 500 },
  { ruleType: 'Cambio de aceite motor mediano', intervalKm: 7000, thresholdKm: 700 },
  { ruleType: 'Cambio de aceite motor pesado', intervalKm: 15000, thresholdKm: 1500 },

  // 🌬️ FILTRO DE AIRE
  { ruleType: 'Cambio de filtro de aire liviano', intervalKm: 10000, thresholdKm: 1000 },
  { ruleType: 'Cambio de filtro de aire mediano', intervalKm: 12000, thresholdKm: 1200 },
  { ruleType: 'Cambio de filtro de aire pesado', intervalKm: 20000, thresholdKm: 2000 },

  // 🛢️ FILTRO DE ACEITE
  { ruleType: 'Cambio de filtro de aceite liviano', intervalKm: 5000, thresholdKm: 500 },
  { ruleType: 'Cambio de filtro de aceite mediano', intervalKm: 7000, thresholdKm: 700 },
  { ruleType: 'Cambio de filtro de aceite pesado', intervalKm: 15000, thresholdKm: 1500 },

  // ⛽ FILTRO DE COMBUSTIBLE
  { ruleType: 'Cambio de filtro de combustible liviano', intervalKm: 20000, thresholdKm: 2000 },
  { ruleType: 'Cambio de filtro de combustible mediano', intervalKm: 25000, thresholdKm: 2500 },
  { ruleType: 'Cambio de filtro de combustible pesado', intervalKm: 40000, thresholdKm: 4000 },

  // 🛞 LLANTAS
  { ruleType: 'Rotacion de llantas liviano', intervalKm: 10000, thresholdKm: 1000 },
  { ruleType: 'Rotacion de llantas mediano', intervalKm: 12000, thresholdKm: 1200 },
  { ruleType: 'Rotacion de llantas pesado', intervalKm: 20000, thresholdKm: 2000 },

  { ruleType: 'Cambio de llantas liviano', intervalKm: 40000, thresholdKm: 3000 },
  { ruleType: 'Cambio de llantas mediano', intervalKm: 50000, thresholdKm: 4000 },
  { ruleType: 'Cambio de llantas pesado', intervalKm: 80000, thresholdKm: 8000 },

  // 🛑 FRENOS
  { ruleType: 'Cambio de pastillas de freno liviano', intervalKm: 20000, thresholdKm: 2000 },
  { ruleType: 'Cambio de pastillas de freno mediano', intervalKm: 25000, thresholdKm: 2500 },
  { ruleType: 'Cambio de pastillas de freno pesado', intervalKm: 30000, thresholdKm: 3000 },

  { ruleType: 'Cambio de discos de freno liviano', intervalKm: 40000, thresholdKm: 3000 },
  { ruleType: 'Cambio de discos de freno mediano', intervalKm: 50000, thresholdKm: 4000 },
  { ruleType: 'Cambio de discos de freno pesado', intervalKm: 60000, thresholdKm: 5000 },

  // 🔋 BATERÍA
  { ruleType: 'Revision de bateria liviano', intervalKm: 15000, thresholdKm: 1500 },
  { ruleType: 'Revision de bateria mediano', intervalKm: 20000, thresholdKm: 2000 },
  { ruleType: 'Revision de bateria pesado', intervalKm: 30000, thresholdKm: 3000 },

  // ❄️ REFRIGERANTE
  { ruleType: 'Cambio de refrigerante liviano', intervalKm: 40000, thresholdKm: 4000 },
  { ruleType: 'Cambio de refrigerante mediano', intervalKm: 50000, thresholdKm: 5000 },
  { ruleType: 'Cambio de refrigerante pesado', intervalKm: 80000, thresholdKm: 8000 },

  // ⚙️ TRANSMISIÓN
  { ruleType: 'Cambio de aceite de transmision liviano', intervalKm: 40000, thresholdKm: 4000 },
  { ruleType: 'Cambio de aceite de transmision mediano', intervalKm: 60000, thresholdKm: 6000 },
  { ruleType: 'Cambio de aceite de transmision pesado', intervalKm: 100000, thresholdKm: 10000 },

  // 🧊 AIRE ACONDICIONADO
  { ruleType: 'Mantenimiento de aire acondicionado liviano', intervalKm: 20000, thresholdKm: 2000 },
  { ruleType: 'Mantenimiento de aire acondicionado mediano', intervalKm: 25000, thresholdKm: 2500 },
  { ruleType: 'Mantenimiento de aire acondicionado pesado', intervalKm: 30000, thresholdKm: 3000 },

  // ⚖️ ALINEACIÓN Y BALANCEO
  { ruleType: 'Alineacion y balanceo liviano', intervalKm: 10000, thresholdKm: 1000 },
  { ruleType: 'Alineacion y balanceo mediano', intervalKm: 12000, thresholdKm: 1200 },
  { ruleType: 'Alineacion y balanceo pesado', intervalKm: 20000, thresholdKm: 2000 },

  // 🛞 SUSPENSIÓN
  { ruleType: 'Revision de suspension liviano', intervalKm: 30000, thresholdKm: 3000 },
  { ruleType: 'Revision de suspension mediano', intervalKm: 40000, thresholdKm: 4000 },
  { ruleType: 'Revision de suspension pesado', intervalKm: 60000, thresholdKm: 6000 },

  // ⚡ SISTEMA ELÉCTRICO
  { ruleType: 'Revision sistema electrico liviano', intervalKm: 20000, thresholdKm: 2000 },
  { ruleType: 'Revision sistema electrico mediano', intervalKm: 25000, thresholdKm: 2500 },
  { ruleType: 'Revision sistema electrico pesado', intervalKm: 40000, thresholdKm: 4000 },

  // 🧯 BUJÍAS
  { ruleType: 'Cambio de bujias liviano', intervalKm: 30000, thresholdKm: 3000 },
  { ruleType: 'Cambio de bujias mediano', intervalKm: 40000, thresholdKm: 4000 },
  { ruleType: 'Cambio de bujias pesado', intervalKm: 60000, thresholdKm: 6000 },

  // 🚰 LIMPIAPARABRISAS
  { ruleType: 'Cambio de plumillas limpiaparabrisas liviano', intervalKm: 10000, thresholdKm: 1000 },
  { ruleType: 'Cambio de plumillas limpiaparabrisas mediano', intervalKm: 12000, thresholdKm: 1200 },
  { ruleType: 'Cambio de plumillas limpiaparabrisas pesado', intervalKm: 15000, thresholdKm: 1500 },
];