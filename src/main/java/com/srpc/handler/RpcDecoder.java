package com.srpc.handler;

import com.srpc.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.util.SerializationUtils;

import java.util.List;

/**
 * Created by zynb0319 on 2016/1/23.
 */
public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;
    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes()<4){
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if(dataLength<0){
            ctx.close();
        }
        if(in.readableBytes()<dataLength){
            in.resetReaderIndex();
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj = SerializationUtil.deserialize(data,genericClass);
        out.add(obj);
    }
}
