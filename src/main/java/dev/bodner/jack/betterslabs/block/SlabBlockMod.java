package dev.bodner.jack.betterslabs.block;

import dev.bodner.jack.betterslabs.block.enums.SlabTypeMod;
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

public class SlabBlockMod extends Block implements Waterloggable {
    public static final EnumProperty<SlabTypeMod> TYPE;
    public static final EnumProperty<SlabType> OLD_TYPE;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape BOTTOM_SHAPE;
    protected static final VoxelShape TOP_SHAPE;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape SOUTH_SHAPE;
    private static final VoxelShape EAST_SHAPE;
    private static final VoxelShape WEST_SHAPE;
    private StateManager<Block, BlockState> state;


    public SlabBlockMod(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(OLD_TYPE, SlabType.BOTTOM).with(WATERLOGGED, false));
        this.setDefaultState(this.getDefaultState().with(OLD_TYPE, SlabType.BOTTOM).with(WATERLOGGED, false).with(TYPE, SlabTypeMod.BOTTOM));
    }

    public boolean hasSidedTransparency(BlockState state) {
        if (state.get(TYPE) == SlabTypeMod.DOUBLE){
            return false;
        }if (state.get(TYPE) == SlabTypeMod.DOUBLEX){
            return false;
        }if (state.get(TYPE) == SlabTypeMod.DOUBLEZ){
            return false;
        }else {return true;}
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OLD_TYPE, WATERLOGGED,TYPE);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        SlabTypeMod slabType = (SlabTypeMod)state.get(TYPE);
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

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = ctx.getWorld().getBlockState(blockPos);
        if (blockState.isOf(this)) {
             if (blockState.equals(blockState.with(TYPE,SlabTypeMod.TOP)) || blockState.equals(blockState.with(TYPE,SlabTypeMod.BOTTOM))){
                 return blockState.with(TYPE, SlabTypeMod.DOUBLE).with(WATERLOGGED, false);
             }if (blockState.equals(blockState.with(TYPE,SlabTypeMod.NORTH)) || blockState.equals(blockState.with(TYPE,SlabTypeMod.SOUTH))){
                return blockState.with(TYPE, SlabTypeMod.DOUBLEZ).with(WATERLOGGED, false);
            }if (blockState.equals(blockState.with(TYPE,SlabTypeMod.EAST)) || blockState.equals(blockState.with(TYPE,SlabTypeMod.WEST))){
                return blockState.with(TYPE, SlabTypeMod.DOUBLEX).with(WATERLOGGED, false);
            }else{
                 return blockState;
            }
        } else {
            FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
            BlockState blockState2 = this.getDefaultState().with(TYPE, SlabTypeMod.BOTTOM).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
            Direction direction = ctx.getSide();
            switch (direction){
                case SOUTH:
                    return blockState2.with(TYPE,SlabTypeMod.NORTH);
                case NORTH:
                    return blockState2.with(TYPE,SlabTypeMod.SOUTH);
                case WEST:
                    return blockState2.with(TYPE,SlabTypeMod.EAST);
                case EAST:
                    return blockState2.with(TYPE,SlabTypeMod.WEST);
                case DOWN:
                    return blockState2.with(TYPE,SlabTypeMod.TOP);
                default: return blockState2;
            }
        }
    }

    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        ItemStack itemStack = context.getStack();
        SlabTypeMod slabType = state.get(TYPE);
        if (itemStack.getItem() == this.asItem()){
            Direction direction = context.getSide();
            switch (slabType){
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
                    return  direction == Direction.EAST;
                default:
                    return true;
            }
        } else {return false;}
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        switch (state.get(TYPE)){
            case DOUBLE:
            case DOUBLEZ:
            case DOUBLEX:
                return false;
            default:
                Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
                return true;
        }
    }

    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        switch (state.get(TYPE)) {
            case DOUBLE:
            case DOUBLEZ:
            case DOUBLEX:
                return false;
            default:
                Waterloggable.super.canFillWithFluid(world, pos, state, fluid);
                return true;
        }
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        switch(type) {
            case WATER:
                return world.getFluidState(pos).isIn(FluidTags.WATER);
            default:
                return false;
        }
    }

    static {
        TYPE = EnumProperty.of("new_type", SlabTypeMod.class);
        OLD_TYPE = EnumProperty.of("type", SlabType.class);
        WATERLOGGED = Properties.WATERLOGGED;
        BOTTOM_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
        TOP_SHAPE = Block.createCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        NORTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
        SOUTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
        EAST_SHAPE = Block.createCuboidShape(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        WEST_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    }
}
