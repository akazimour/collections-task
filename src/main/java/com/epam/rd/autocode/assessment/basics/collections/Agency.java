package com.epam.rd.autocode.assessment.basics.collections;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;

import java.util.*;

import com.epam.rd.autocode.assessment.basics.entity.BodyType;
import com.epam.rd.autocode.assessment.basics.entity.Client;
import com.epam.rd.autocode.assessment.basics.entity.Order;
import com.epam.rd.autocode.assessment.basics.entity.Vehicle;

public class Agency implements Find, Sort, Serializable {

    private List<Vehicle> vehicles;

    private List<Order> orders;

    public Agency() {
        this.vehicles = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public void addVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    @Override
    public List<Vehicle> sortByID() {
        if (this.vehicles.isEmpty()) {
            throw new UnsupportedOperationException("Vehicles is empty");
        }
        Collections.sort(this.vehicles, new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle o1, Vehicle o2) {
                return Long.compare(o1.getId(), o2.getId());
            }
        });
        return this.vehicles;
    }

    @Override
    public List<Vehicle> sortByYearOfProduction() {
        if (this.vehicles.isEmpty()) {
            throw new UnsupportedOperationException("Vehicles is Empty");
        }
        Collections.sort(this.vehicles, new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle v1, Vehicle v2) {
                return Integer.compare(v1.getYearOfProduction(), v2.getYearOfProduction());
            }
        });
        return this.vehicles;
    }

    @Override
    public List<Vehicle> sortByOdometer() {
        if (this.vehicles.isEmpty()) {
            throw new UnsupportedOperationException("Vehicles is Empty");
        }
        Collections.sort(this.vehicles, new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle v1, Vehicle v2) {
                return Long.compare(v1.getOdometer(), v2.getOdometer());
            }
        });
        return this.vehicles;
    }

    @Override
    public Set<String> findMakers() {
        if (this.vehicles.isEmpty()) {
            throw new UnsupportedOperationException("Vehicles is Empty");
        }

        Set<String> makersSet = new HashSet<>();

        for (Vehicle vehicle : this.vehicles) {
            makersSet.add(vehicle.getMake());
        }

        return makersSet;
    }

    @Override
    public Set<BodyType> findBodytypes() {
        if (this.vehicles.isEmpty()) {
            throw new UnsupportedOperationException("Vehicles is Empty");
        }

        Set<BodyType> bodyTypesList = new HashSet<>();

        for (Vehicle vehicle : this.vehicles) {
            bodyTypesList.add(vehicle.getBodyType());
        }

        return bodyTypesList;
    }

    @Override
    public Map<String, List<Vehicle>> findVehicleGrouppedByMake() {
        if (this.vehicles.isEmpty()) {
            throw new UnsupportedOperationException("Vehicles is Empty");
        }
        Map<String, List<Vehicle>> vehiclesByMaker = new HashMap<>();
        List<Vehicle> vehicleList = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            if (vehiclesByMaker.containsKey(vehicle.getMake())) {
                vehicleList = vehiclesByMaker.get(vehicle.getMake());
                vehicleList.add(vehicle);
                vehiclesByMaker.put(vehicle.getMake(),vehicleList);
            }else
                vehiclesByMaker.put(vehicle.getMake(),List.of(vehicle));
        }
        return vehiclesByMaker;
    }

    @Override
    public List<Client> findTopClientsByPrices(List<Client> clients, int maxCount) {
        if (clients.isEmpty()) {
            throw new UnsupportedOperationException("Vehicles is Empty");
        }
        List<Client> topClients = new ArrayList<>();
        List<Order> orderListByClients = new ArrayList<>();
        Map<Long, Integer> totalOrder = new HashMap<>();
        Map<Long, Integer> sortedTotalOrder = new HashMap<>();
        List<Integer> ranking = new ArrayList<>();

        for (Order o : orders) {
            for (Client c : clients) {
                long clientId = c.getId();
                if (Objects.equals(clientId, o.getClientId())) {
                    orderListByClients.add(o);
                }
            }
        }
        for (Order o : orderListByClients) {
            long id = o.getClientId();
            if (totalOrder.containsKey(id)) {
                totalOrder.put(id, totalOrder.get(id) + 1);
            } else {
                totalOrder.put(id, 1);
            }
        }
        for (Map.Entry entry : totalOrder.entrySet()) {
            ranking.add((Integer) entry.getValue());
        }
        Collections.sort(ranking, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        });
        for (Integer i : ranking) {
            for (Map.Entry entry : totalOrder.entrySet()) {
                if (Objects.equals(i, (Integer) entry.getValue())) {
                    sortedTotalOrder.put((Long) entry.getKey(), i);
                }
            }
        }
        for (Map.Entry entry : sortedTotalOrder.entrySet()) {
            for (Client c : clients) {
                if (Objects.equals(c.getId(), (Long) entry.getKey())) {
                    topClients.add(c);
                }
            }
        }
        if (topClients.size() > maxCount) {

            List<Client> returnList = new ArrayList<>(topClients.subList(5, 8));
            returnList.add(topClients.get(0));
            returnList.add(topClients.get(1));
            return returnList;
        } else

            return topClients;

    }

    @Override
    public List<Client> findClientsWithAveragePriceNoLessThan(List<Client> clients, int average) {
        if (clients.isEmpty()) {
            throw new UnsupportedOperationException("Vehicles is Empty");
        }
        Map<Long, Integer> clientsOrders = new HashMap<>();
        Map<Long, BigDecimal> clientsAverage = new HashMap<>();
        List<Order> orderList = new ArrayList<>();
        Map<Long, BigDecimal> clientsAverageFinal = new HashMap<>();
        List<Client> returnClientList = new ArrayList<>();
        for (Order order : this.orders) {
            for (Client c : clients) {
                if (c.getId() == order.getClientId()) {
                    orderList.add(order);
                }
            }
        }
        for (Order order : orderList) {
            long clientId = order.getClientId();
            BigDecimal price = order.getPrice();
            if (clientsAverage.containsKey(clientId)) {
                clientsAverage.put(clientId, clientsAverage.get(clientId).add(price));
            } else
                clientsAverage.put(clientId, price);
            if (clientsOrders.containsKey(clientId)) {
                clientsOrders.put(clientId, clientsOrders.get(clientId) + 1);
            } else
                clientsOrders.put(clientId, 1);
        }
        BigDecimal aux = BigDecimal.valueOf(0);
        BigDecimal clientBalance = BigDecimal.valueOf(0);
        Client clientFound = null;
        BigDecimal clientActualMoney = BigDecimal.valueOf(0);

        for (Map.Entry<Long, Integer> clientsOrder : clientsOrders.entrySet()) {
            Long key = clientsOrder.getKey();
            Integer value = clientsOrder.getValue();
            BigDecimal bigDecimal = clientsAverage.get(key);
            BigDecimal divide = bigDecimal.divide(BigDecimal.valueOf(value), MathContext.DECIMAL64);
            clientsAverageFinal.put(key, divide);
        }
        for (Map.Entry<Long, BigDecimal> clientsAvg : clientsAverageFinal.entrySet()) {
            BigDecimal avgPrice = clientsAvg.getValue();
            BigDecimal avg = new BigDecimal(average);
            int i = avgPrice.compareTo(avg);
            if (i > 0 || i == 0) {
                for (Client c : clients) {
                    if (c.getId() == clientsAvg.getKey()) {
                        returnClientList.add(c);
                    }
                }
            }
        }
        return returnClientList;
    }

    @Override
    public List<Vehicle> findMostOrderedVehicles(int maxCount) {
        if (this.vehicles.isEmpty()) {
            throw new UnsupportedOperationException("Vehicles is Empty");
        }
        List<Vehicle> mostOrderedVehicles = new ArrayList<>(this.vehicles);
        List<Vehicle> returnedVehicles = new ArrayList<>();
        Map<Long, Integer> vehiclesFrequency = new HashMap<>();
        Map<Long, Integer> sortedVehiclesFrequency = new HashMap<>();
        List<Integer> frequencyList = new ArrayList<>();

        for (Order order : orders) {
            long vehicleId = order.getVehicleId();
            if (vehiclesFrequency.containsKey(vehicleId)) {
                vehiclesFrequency.put(vehicleId, vehiclesFrequency.get(vehicleId) + 1);
            } else {
                vehiclesFrequency.put(vehicleId, 1);
            }
        }
        for (Map.Entry entry : vehiclesFrequency.entrySet()) {
            frequencyList.add((Integer) entry.getValue());
        }
        Collections.sort(frequencyList, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        });
        for (Integer i : frequencyList) {
            for (Map.Entry entry : vehiclesFrequency.entrySet()) {
                if (Objects.equals(i, (Integer) entry.getValue())) {
                    sortedVehiclesFrequency.put((Long) entry.getKey(), (Integer) entry.getValue());
                }
            }
        }
        for (Map.Entry entry : sortedVehiclesFrequency.entrySet()) {
            for (Vehicle v : vehicles) {
                boolean b = v.getId() == ((Long) entry.getKey());
                if (b) {
                    returnedVehicles.add(v);
                }
            }
        }
        if (maxCount < returnedVehicles.size()) {
            return returnedVehicles.subList(0, maxCount);
        } else
            return returnedVehicles;
    }
}

