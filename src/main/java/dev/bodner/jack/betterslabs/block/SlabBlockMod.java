package dev.bodner.jack.betterslabs.block;

import dev.bodner.jack.betterslabs.Betterslabs;
import dev.bodner.jack.betterslabs.client.BetterslabsClient;
import dev.bodner.jack.betterslabs.component.Components;
import io.netty.buffer.Unpooled;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
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
    public static final EnumProperty<Direction.Axis> AXIS;
    public static final EnumProperty<SlabType> TYPE;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape BOTTOM_SHAPE;
    protected static final VoxelShape TOP_SHAPE;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape SOUTH_SHAPE;
    private static final VoxelShape EAST_SHAPE;
    private static final VoxelShape WEST_SHAPE;


    public SlabBlockMod(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false).with(TYPE, SlabType.BOTTOM).with(AXIS, Direction.Axis.Y));
    }

    public boolean hasSidedTransparency(BlockState state) {
        if (state.get(TYPE) == SlabType.DOUBLE) {
            return false;
        }else {return true;}
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED,TYPE,AXIS);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        SlabType slabType = state.get(TYPE);
        Direction.Axis axis = state.get(AXIS);
        switch(axis) {
            case X:
                switch (slabType){
                    case TOP:
                        return EAST_SHAPE;
                    case BOTTOM:
                        return WEST_SHAPE;
                }
            case Y:
                switch (slabType) {
                    case TOP:
                        return TOP_SHAPE;
                    case BOTTOM:
                        return BOTTOM_SHAPE;
                }
            case Z:
                switch (slabType){
                    case TOP:
                        return SOUTH_SHAPE;
                    case BOTTOM:
                        return NORTH_SHAPE;
                }
            default:
                return VoxelShapes.fullCube();
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = ctx.getWorld().getBlockState(blockPos);
        if (blockState.isOf((this))) {
            if (blockState.equals(blockState.with(TYPE,SlabType.TOP)) || blockState.equals(blockState.with(TYPE,SlabType.BOTTOM))){
                return blockState.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false).with(AXIS,blockState.get(AXIS));
            }else{
                return blockState;
            }
        } else {
            FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
            BlockState blockState2 = (this).getDefaultState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(AXIS, Direction.Axis.Y);
            Direction direction = ctx.getSide();

                switch (Components.MODE_KEY.get(ctx.getPlayer()).getPlaceMode()) {
                    case HORIZONTAL:
                        blockState2.with(AXIS, Direction.Axis.Y);
                        return direction != Direction.DOWN && (direction == Direction.UP || ctx.getHitPos().y - (double) blockPos.getY() <= 0.5D) ? blockState2 : blockState2.with(TYPE, SlabType.TOP);
                    case VERTICAL:
                        double xPos = (ctx.getHitPos().x - (double) blockPos.getX()) - 0.5D;
                        double zPos = (ctx.getHitPos().z - (double) blockPos.getZ()) - 0.5D;

                        if (direction == Direction.DOWN || direction == Direction.UP) {
                            if (Math.abs(xPos) > Math.abs(zPos)) {
                                if (xPos <= 0) {
                                    return blockState2.with(TYPE, SlabType.BOTTOM).with(AXIS, Direction.Axis.X);
                                } else {
                                    return blockState2.with(TYPE, SlabType.TOP).with(AXIS, Direction.Axis.X);
                                }
                            } else {
                                if (zPos <= 0) {
                                    return blockState2.with(TYPE, SlabType.BOTTOM).with(AXIS, Direction.Axis.Z);
                                } else {
                                    return blockState2.with(TYPE, SlabType.TOP).with(AXIS, Direction.Axis.Z);
                                }
                            }
                        }

                    default:
                        switch (direction) {
                            case NORTH:
                                return blockState2.with(TYPE, SlabType.TOP).with(AXIS, Direction.Axis.Z);
                            case SOUTH:
                                return blockState2.with(TYPE, SlabType.BOTTOM).with(AXIS, Direction.Axis.Z);
                            case EAST:
                                return blockState2.with(TYPE, SlabType.BOTTOM).with(AXIS, Direction.Axis.X);
                            case WEST:
                                return blockState2.with(TYPE, SlabType.TOP).with(AXIS, Direction.Axis.X);
                            case DOWN:
                                return blockState2.with(TYPE, SlabType.TOP).with(AXIS, Direction.Axis.Y);
                            default:
                                return blockState2;
                        }
                }

        }
    }


    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        ItemStack itemStack = context.getStack();
        SlabType slabType = state.get(TYPE);
        Direction.Axis axis = state.get(AXIS);
        if (itemStack.getItem() == (this).asItem()){
            Direction direction = context.getSide();
            boolean bl = context.getHitPos().y - (double)context.getBlockPos().getY() > 0.5D;
            boolean blz = context.getHitPos().z - (double)context.getBlockPos().getZ() > 0.5D;
            boolean blx = context.getHitPos().x - (double)context.getBlockPos().getX() > 0.5D;

//            if(context.getWorld().isClient){
//                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
//                passedData.writeEnumConstant(BetterslabsClient.mode);
//            }
            switch (Components.MODE_KEY.get(context.getPlayer()).getPlaceMode()){
                case HORIZONTAL:
                    if (context.canReplaceExisting()){
                        if (axis == Direction.Axis.Y){
                            switch (slabType){
                                case TOP:
                                    return direction == Direction.DOWN || !bl && direction.getAxis().isHorizontal();
                                case BOTTOM:
                                    return direction == Direction.UP || bl && direction.getAxis().isHorizontal();
                                default:
                                    return false;
                            }
                        }

                    }else {
                        return true;
                    }

                case VERTICAL:
                    if (context.canReplaceExisting()){
                        if (direction == Direction.DOWN || direction == Direction.UP){
                            return false;
                        }
                        if (axis == Direction.Axis.X){
                            switch (slabType){
                                case TOP:
                                    return direction == Direction.WEST || blx && direction.getAxis().isVertical();
                                case BOTTOM:
                                    return direction == Direction.EAST || !blx && direction.getAxis().isVertical();
                                default:
                                    return false;
                            }
                        }
                        if (axis == Direction.Axis.Z){
                            switch (slabType){
                                case TOP:
                                    return direction == Direction.NORTH || blz && direction.getAxis().isVertical();
                                case BOTTOM:
                                    return direction == Direction.SOUTH || !blz && direction.getAxis().isVertical();
                                default:
                                    return false;
                            }
                        }
                    }else {
                        return true;
                    }

                default:
                    if (axis == Direction.Axis.Y){
                        switch (slabType){
                            case BOTTOM:
                                return direction == Direction.UP;
                            case TOP:
                                return direction == Direction.DOWN;
                            default:
                                return false;
                        }
                    }
                    if (axis == Direction.Axis.X){
                        switch (slabType){
                            case BOTTOM:
                                return direction == Direction.EAST;
                            case TOP:
                                return direction == Direction.WEST;
                            default:
                                return false;
                        }
                    }
                    if (axis == Direction.Axis.Z){
                        switch (slabType){
                            case BOTTOM:
                                return direction == Direction.SOUTH;
                            case TOP:
                                return direction == Direction.NORTH;
                            default:
                                return false;
                        }
                    }
                    else {
                        return false;
                    }

                }
            }else {
                return false;
        }
    }


    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.get(TYPE) == SlabType.DOUBLE) {
            return false;
        }
        Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState);
        return true;
    }

    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        if (state.get(TYPE) == SlabType.DOUBLE) {
            return false;
        }
        Waterloggable.super.canFillWithFluid(world, pos, state, fluid);
        return true;
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
        TYPE = Properties.SLAB_TYPE;
        WATERLOGGED = Properties.WATERLOGGED;
        AXIS = Properties.AXIS;
        BOTTOM_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
        TOP_SHAPE = Block.createCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        NORTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
        SOUTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
        EAST_SHAPE = Block.createCuboidShape(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        WEST_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    }
}
