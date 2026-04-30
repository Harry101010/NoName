package aptech.proj_NN_group2.controller.production;

/**
 * Legacy compatibility class.
 *
 * The project currently uses RecipeManagementController together with
 * recipe_management_cleaned.fxml (and the main menu now points to the same
 * working screen content through recipe_management.fxml).
 *
 * Keeping this class as a thin extension prevents source compilation failures
 * without changing the live business flow.
 */
@Deprecated
public class RecipeManagementController2 extends RecipeManagementController {
}