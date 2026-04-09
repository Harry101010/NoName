package aptech.proj_NN_group2.test;

import aptech.proj_NN_group2.model.business.repository.IceCreamRepository;
import aptech.proj_NN_group2.model.business.repository.ProductionOrderRepository;
import aptech.proj_NN_group2.model.business.repository.ProductionStageRepository;
import aptech.proj_NN_group2.model.entity.IceCream;
import aptech.proj_NN_group2.model.entity.ProductionOrder;
import aptech.proj_NN_group2.model.entity.ProductionStage;

public class TestDuy {
    public static void main(String[] args) {
        System.out.println("=== Test IceCreamRepository ===");
        IceCreamRepository iceCreamRepo = new IceCreamRepository();
        for (IceCream ic : iceCreamRepo.findAllActive()) {
            System.out.println(ic.getIce_cream_id() + " - " + ic.getIce_cream_name());
        }

        System.out.println("\n=== Test ProductionOrderRepository ===");
        ProductionOrderRepository orderRepo = new ProductionOrderRepository();
        for (ProductionOrder o : orderRepo.findAll()) {
            System.out.println("ID: " + o.getProduction_order_id()
                    + " | Kem: " + o.getIce_cream_name()
                    + " | Kg: " + o.getPlanned_output_kg()
                    + " | Status: " + o.getOrder_status());
        }

        System.out.println("\n=== Test ProductionStageRepository (order ID=1) ===");
        ProductionStageRepository stageRepo = new ProductionStageRepository();
        for (ProductionStage s : stageRepo.findByOrderId(1)) {
            System.out.println("Stage " + s.getStage_no()
                    + " - " + s.getStage_name()
                    + " | Status: " + s.getStage_status());
        }
    }
}
