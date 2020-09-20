package dev.bodner.jack.betterslabs.mixin;

import dev.bodner.jack.betterslabs.Betterslabs;
import dev.bodner.jack.betterslabs.client.BetterslabsClient;
import dev.bodner.jack.betterslabs.enums.SlabTypeMod;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlabBlock.class)
abstract class SlabMixin extends Block implements Waterloggable{

    @Shadow @Final public static EnumProperty<SlabType> TYPE;
    private static final EnumProperty<SlabTypeMod> NEW_TYPE = EnumProperty.of("new_type", SlabTypeMod.class);
    @Shadow @Final public static BooleanProperty WATERLOGGED;
    private static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    private static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);

    public SlabMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(AbstractBlock.Settings settings, CallbackInfo ci){
        this.setDefaultState(((SlabBlock)(Object)this).getDefaultState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, false).with(NEW_TYPE, SlabTypeMod.BOTTOM));
    }

    /**
     * @author
     * Eilux
     * @reason
     * functionality
     */
    @Overwrite
    public boolean hasSidedTransparency(BlockState state) {
        if (state.get(NEW_TYPE) == SlabTypeMod.DOUBLE){
            return false;
        }if (state.get(NEW_TYPE) == SlabTypeMod.DOUBLEX){
            return false;
        }
        return state.get(NEW_TYPE) != SlabTypeMod.DOUBLEZ;
    }

    /**
     * @author
     * Eilux
     * @reason
     * functionality
     */
    @Overwrite
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TYPE, WATERLOGGED,NEW_TYPE);
    }

    /**
     * @author
     * Eilux
     * @reason
     * functionality
     */
    @Overwrite
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        SlabTypeMod slabType = state.get(NEW_TYPE);
        switch(slabType) {
            case DOUBLEX:
            case DOUBLEZ:
            case DOUBLE:
                return VoxelShapes.fullCube();
            case TOP:
                return TOP_SHAPE;
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            default:
                return BOTTOM_SHAPE;
        }
    }

    /**
     * @author
     * Eilux
     * @reason
     * functionality
     */

    @Nullable
    @Overwrite
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = ctx.getWorld().getBlockState(blockPos);
        if (blockState.isOf(((SlabBlock)(Object)this))) {
            if (blockState.equals(blockState.with(NEW_TYPE,SlabTypeMod.TOP)) || blockState.equals(blockState.with(NEW_TYPE,SlabTypeMod.BOTTOM))){
                return blockState.with(NEW_TYPE, SlabTypeMod.DOUBLE).with(WATERLOGGED, false);
            }if (blockState.equals(blockState.with(NEW_TYPE,SlabTypeMod.NORTH)) || blockState.equals(blockState.with(NEW_TYPE,SlabTypeMod.SOUTH))){
                return blockState.with(NEW_TYPE, SlabTypeMod.DOUBLEZ).with(WATERLOGGED, false);
            }if (blockState.equals(blockState.with(NEW_TYPE,SlabTypeMod.EAST)) || blockState.equals(blockState.with(NEW_TYPE,SlabTypeMod.WEST))){
                return blockState.with(NEW_TYPE, SlabTypeMod.DOUBLEX).with(WATERLOGGED, false);
            }else{
                return blockState;
            }
        } else {
            FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
            BlockState blockState2 = ((SlabBlock)(Object)this).getDefaultState().with(NEW_TYPE, SlabTypeMod.BOTTOM).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
            Direction direction = ctx.getSide();
            switch (BetterslabsClient.mode){
                case HORIZONTAL:
                    return direction != Direction.DOWN && (direction == Direction.UP || ctx.getHitPos().y - (double)blockPos.getY() <= 0.5D) ? blockState2 : blockState2.with(NEW_TYPE, SlabTypeMod.TOP);
                case VERTICAL:
                    double xPos = (ctx.getHitPos().x - (double)blockPos.getX()) - 0.5D;
                    double zPos = (ctx.getHitPos().z - (double)blockPos.getZ()) - 0.5D;

                    if (direction == Direction.DOWN || direction == Direction.UP){
                        if(Math.abs(xPos)>Math.abs(zPos)){
                            if(xPos <= 0){
                                return blockState2.with(NEW_TYPE,SlabTypeMod.WEST);
                            }else {
                                return blockState2.with(NEW_TYPE,SlabTypeMod.EAST);
                            }
                        }else {
                            if(zPos <= 0){
                                return blockState2.with(NEW_TYPE,SlabTypeMod.NORTH);
                            }else {
                                return blockState2.with(NEW_TYPE,SlabTypeMod.SOUTH);
                            }
                        }
                    }

                default:
                    switch (direction){
                        case SOUTH:
                            return blockState2.with(NEW_TYPE,SlabTypeMod.NORTH);
                        case NORTH:
                            return blockState2.with(NEW_TYPE,SlabTypeMod.SOUTH);
                        case WEST:
                            return blockState2.with(NEW_TYPE,SlabTypeMod.EAST);
                        case EAST:
                            return blockState2.with(NEW_TYPE,SlabTypeMod.WEST);
                        case DOWN:
                            return blockState2.with(NEW_TYPE,SlabTypeMod.TOP);
                        default: return blockState2;
                    }
            }
        }
    }

    /**
     * @author
     * Eilux
     * @reason
     * functionality
     */
    @Overwrite
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        ItemStack itemStack = context.getStack();
        SlabTypeMod slabType = state.get(NEW_TYPE);
        if (itemStack.getItem() == ((SlabBlock)(Object)this).asItem()){
            Direction direction = context.getSide();
            boolean bl = context.getHitPos().y - (double)context.getBlockPos().getY() > 0.5D;
            boolean blz = context.getHitPos().z - (double)context.getBlockPos().getZ() > 0.5D;
            boolean blx = context.getHitPos().x - (double)context.getBlockPos().getX() > 0.5D;

            switch (BetterslabsClient.mode){
                case HORIZONTAL:
                    if (context.canReplaceExisting()){
                        switch (slabType){
                            case TOP:
                                return direction == Direction.DOWN || !bl && direction.getAxis().isHorizontal();
                            case BOTTOM:
                                return direction == Direction.UP || bl && direction.getAxis().isHorizontal();
                            default:
                                return false;
                        }
                    }else {
                        return true;
                    }

                case VERTICAL:
                    if (context.canReplaceExisting()){
                        if (direction == Direction.DOWN || direction == Direction.UP){
                            return false;
                        }
                        switch (slabType){
                            case NORTH:
                                return direction == Direction.SOUTH || !blz && direction.getAxis().isVertical();
                            case SOUTH:
                                return direction == Direction.NORTH || blz && direction.getAxis().isVertical();
                            case EAST:
                                return direction == Direction.WEST || blx && direction.getAxis().isVertical();
                            case WEST:
                                return direction == Direction.EAST || !blx && direction.getAxis().isVertical();
                            default:
                                return false;
                        }
                    }else {
                        return true;
                    }

                default:
                    switch (slabType) {
                        case DOUBLEX:
                        case DOUBLEZ:
                        case DOUBLE:
                            return false;
                        case BOTTOM:
                            return direction == Direction.UP;
                        case TOP:
                            return direction == Direction.DOWN;
                        case NORTH:
                            return direction == Direction.SOUTH;
                        case SOUTH:
                            return direction == Direction.NORTH;
                        case EAST:
                            return direction == Direction.WEST;
                        case WEST:
                            return direction == Direction.EAST;
                        default:
                            return true;
                    }
            }
        }else {
            return false;
        }
    }

    /**
     * @author
     * Eilux
     * @reason
     * functionality
     */
    @Overwrite
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    /**
     * @author
     * Eilux
     * @reason
     * functionality
     */
    @Overwrite
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        switch (state.get(NEW_TYPE)){
            case DOUBLE:
            case DOUBLEZ:
            case DOUBLEX:
                return false;
            default:
                if (!(Boolean)state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
                    if (!world.isClient()) {
                        world.setBlockState(pos, state.with(Properties.WATERLOGGED, true), 3);
                        world.getFluidTickScheduler().schedule(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
                    }
                    return true;
                } else {
                    return false;
                }
        }
    }

    /**
     * @author
     * Eilux
     * @reason
     * functionality
     */
    @Overwrite
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        switch (state.get(NEW_TYPE)) {
            case DOUBLE:
            case DOUBLEZ:
            case DOUBLEX:
                return false;
            default:
                return !(Boolean)state.get(Properties.WATERLOGGED) && fluid == Fluids.WATER;
        }
    }

    /**
     * @author
     * Eilux
     * @reason
     *functionality
     */
    @Overwrite
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    /**
     * @author
     * Eilux
     * @reason
     * functionality
     */
    @Overwrite
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        if (type == NavigationType.WATER) {
            return world.getFluidState(pos).isIn(FluidTags.WATER);
        }
        return false;
    }
}
