package rpc.core.common.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;


import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer {
    private Random random = new Random();

    @Override
    public Instance select(List<Instance> instances) {
        int index = random.nextInt(instances.size() - 1);
        return instances.get(index);
    }
}
