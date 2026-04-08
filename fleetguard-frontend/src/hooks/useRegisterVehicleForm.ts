import { useState } from "react";
import { vehicleService } from "@/services/vehicle.service";
import { vehicleValidator } from "@/validators/vehicle.validator";
import { CreateVehicleDto } from "@/types";

const initialFormData: CreateVehicleDto = {
  plate: "",
  vin: "",
  brand: "",
  model: "",
  year: "" as unknown as number,
  fuelType: "",
  vehicleTypeId: "",
};

interface UseRegisterVehicleFormReturn {
  formData: CreateVehicleDto;
  loading: boolean;
  plateError: string;
  vinError: string;
  isVinValid: boolean;
  isFormValid: boolean;
  handleChange: (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
  ) => void;
  handleSubmit: (e: React.FormEvent) => Promise<void>;
}

export function useRegisterVehicleForm(
  showToast: (message: string, type: "success" | "error") => void,
): UseRegisterVehicleFormReturn {
  const [formData, setFormData] = useState<CreateVehicleDto>(initialFormData);
  const [loading, setLoading] = useState(false);
  const [plateError, setPlateError] = useState("");
  const [vinError, setVinError] = useState("");

  const isVinValid = formData.vin.length === 17;
  const isFormValid =
    isVinValid &&
    !vehicleValidator.plate(formData.plate) &&
    !vehicleValidator.brand(formData.brand) &&
    !vehicleValidator.model(formData.model) &&
    !vehicleValidator.year(formData.year as number) &&
    !vehicleValidator.fuelType(formData.fuelType) &&
    !vehicleValidator.vehicleTypeId(formData.vehicleTypeId);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
  ) => {
    const { name, value } = e.target;
    const parsedValue =
      name === "plate"
        ? value.toUpperCase()
        : name === "year"
          ? value === ""
            ? ""
            : Number(value)
          : value;

    setFormData((prev) => ({ ...prev, [name]: parsedValue }));
    if (name === "plate") setPlateError("");
    if (name === "vin") setVinError("");
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isFormValid) return;
    setLoading(true);
    try {
      await vehicleService.register(formData);
      showToast("Vehículo registrado correctamente", "success");
      setFormData(initialFormData);
      setPlateError("");
      setVinError("");
    } catch (error: unknown) {
      const err = error as {
        status?: number;
        message?: string;
        field?: string;
        errors?: string[];
      };

      if (err.status === 409) {
        const msg = (err.message ?? "").toLowerCase();
        const field = (err.field ?? "").toLowerCase();

        // Primero intentamos usar el campo discriminador explícito (field)
        // Luego caemos en detección por mensaje
        const isVinDuplicate = field === "vin" || msg.includes("vin");

        const isPlateDuplicate =
          field === "plate" ||
          field === "placa" ||
          msg.includes("placa") ||
          msg.includes("plate");

        if (isVinDuplicate) {
          setVinError("Este VIN ya está registrado en el sistema");
        } else if (isPlateDuplicate) {
          setPlateError("Esta placa ya está registrada en el sistema");
        } else {
          // Si no podemos discriminar, mostramos el mensaje del servidor directamente
          showToast(err.message || "Registro duplicado", "error");
        }
      } else if (err.status === 0) {
        showToast("Sin conexión con el servidor", "error");
      } else if (err.status === 400 && err.errors && err.errors.length > 0) {
        showToast(`Error de validación: ${err.errors.join(", ")}`, "error");
      } else {
        showToast(err.message || "Error al registrar vehículo", "error");
      }
    } finally {
      setLoading(false);
    }
  };

  return {
    formData,
    loading,
    plateError,
    vinError,
    isVinValid,
    isFormValid,
    handleChange,
    handleSubmit,
  };
}
