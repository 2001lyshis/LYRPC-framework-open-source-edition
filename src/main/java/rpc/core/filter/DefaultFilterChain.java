package rpc.core.filter;

import rpc.core.common.entity.RpcRequest;
import rpc.core.common.entity.RpcResponse;
import rpc.core.common.excption.RpcException;

import java.io.IOException;

public class DefaultFilterChain implements RpcFilterChain {
    private RpcFilter[] filters = new RpcFilter[0];
    private int pos = 0;
    private int n = 0;

    @Override
    public void doFilter(RpcRequest request, RpcResponse response) throws IOException, RpcException {
        if(this.pos < this.n) {
            RpcFilter nextFilter = this.filters[this.pos++];
            nextFilter.doFilter(request, response, this);
        }
    }

    public void addFilter(RpcFilter newFilter) {
        RpcFilter[] newFilters = this.filters;
        for (RpcFilter filter : newFilters) {
            if (filter == newFilter) {
                return;
            }
        }

        if(this.n == this.filters.length) {
            newFilters = new RpcFilter[this.n + 10];
            System.arraycopy(newFilters, 0, this.filters, 0, this.n);
            this.filters = newFilters;
        }

        this.filters[this.n++] = newFilter;
    }
}
