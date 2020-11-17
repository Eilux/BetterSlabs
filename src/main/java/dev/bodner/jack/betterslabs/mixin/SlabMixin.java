package dev.bodner.jack.betterslabs.mixin;

import dev.bodner.jack.betterslabs.client.BetterslabsClient;
import dev.bodner.jack.betterslabs.component.Components;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SlabBlock.class)
abstract class SlabMixin {
    @Shadow @Final public static EnumProperty<SlabType> TYPE;
    @Shadow @Final public static BooleanProperty WATERLOGGED;
    @Shadow @Final protected static VoxelShape BOTTOM_SHAPE;
    @Shadow @Final protected static VoxelShape TOP_SHAPE;
    private static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);




    /**
     * @author Eilux
     * @reason functionality
     */
    @Overwrite
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

    /**
     * @author Eilux
     * @reason functionality
     */
    @Overwrite
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = ctx.getWorld().getBlockState(blockPos);
        if (blockState.isOf(((SlabBlock)(Object)this))) {
            if (blockState.equals(blockState.with(TYPE,SlabType.TOP)) || blockState.equals(blockState.with(TYPE,SlabType.BOTTOM))){
                return blockState.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false).with(AXIS,blockState.get(AXIS));
            }else{
                return blockState;
            }
        } else {
            FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
            BlockState blockState2 = ((SlabBlock)(Object)this).getDefaultState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(AXIS, Direction.Axis.Y);
            Direction direction = ctx.getSide();
            switch (Components.MODE_KEY.get(ctx.getPlayer()).getPlaceMode()){
                case HORIZONTAL:
                    blockState2.with(AXIS, Direction.Axis.Y);
                    return direction != Direction.DOWN && (direction == Direction.UP || ctx.getHitPos().y - (double)blockPos.getY() <= 0.5D) ? blockState2 : blockState2.with(TYPE, SlabType.TOP);
                case VERTICAL:
                    double xPos = (ctx.getHitPos().x - (double)blockPos.getX()) - 0.5D;
                    double zPos = (ctx.getHitPos().z - (double)blockPos.getZ()) - 0.5D;

                    if (direction == Direction.DOWN || direction == Direction.UP){
                        if(Math.abs(xPos)>Math.abs(zPos)){
                            if(xPos <= 0){
                                return blockState2.with(TYPE,SlabType.BOTTOM).with(AXIS, Direction.Axis.X);
                            }else {
                                return blockState2.with(TYPE,SlabType.TOP).with(AXIS, Direction.Axis.X);
                            }
                        }else {
                            if(zPos <= 0){
                                return blockState2.with(TYPE,SlabType.BOTTOM).with(AXIS, Direction.Axis.Z);
                            }else {
                                return blockState2.with(TYPE,SlabType.TOP).with(AXIS, Direction.Axis.Z);
                            }
                        }
                    }

                default:
                    switch (direction){
                        case NORTH:
                            return blockState2.with(TYPE,SlabType.TOP).with(AXIS, Direction.Axis.Z);
                        case SOUTH:
                            return blockState2.with(TYPE,SlabType.BOTTOM).with(AXIS, Direction.Axis.Z);
                        case EAST:
                            return blockState2.with(TYPE,SlabType.BOTTOM).with(AXIS, Direction.Axis.X);
                        case WEST:
                            return blockState2.with(TYPE,SlabType.TOP).with(AXIS, Direction.Axis.X);
                        case DOWN:
                            return blockState2.with(TYPE,SlabType.TOP).with(AXIS, Direction.Axis.Y);
                        default: return blockState2;
                    }
            }
        }
    }


    /**
     * @author Eilux
     * @reason functionality
     */
    @Overwrite
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        ItemStack itemStack = context.getStack();
        SlabType slabType = state.get(TYPE);
        Direction.Axis axis = state.get(AXIS);
        if (itemStack.getItem() == ((SlabBlock)(Object)this).asItem()){
            Direction direction = context.getSide();
            boolean bl = context.getHitPos().y - (double)context.getBlockPos().getY() > 0.5D;
            boolean blz = context.getHitPos().z - (double)context.getBlockPos().getZ() > 0.5D;
            boolean blx = context.getHitPos().x - (double)context.getBlockPos().getX() > 0.5D;

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
}
