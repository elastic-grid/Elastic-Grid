deployment(name:'Calculator 2') {
    groups 'elastic-grid'

    artifact id:'service', 'com.elasticgrid.examples.calculator:calculator2:1.0'
    artifact id:'service-dl', 'com.elasticgrid.examples.calculator:calculator2:dl:1.0'

    service(name: 'Calculator') {
        interfaces {
            classes 'calculator.Calculator'
            artifact ref:'service-dl'
        }
        implementation(class:'calculator.service.CalculatorImpl') {
            artifact ref:'service'
        }
        associations {
            association name:'Add', type:'uses', property:'add'
            association name:'Subtract', type:'uses', property:'subtract'
            association name:'Multiply', type:'uses', property:'multiply'
            association name:'Divide', type:'uses', property:'divide'
            association name:'Modulo', type:'uses', property:'modulo'
        }
        sla(id: 'time', high: 400000) {
          policy type: 'scaling', max: 3, lowerDampener: 3000, upperDampener: 3000
          monitor name: 'time', property: 'time', period: 5000
        }
        maintain 1
    }

    ['Add', 'Subtract', 'Multiply', 'Divide', 'Modulo'].each { s ->
        service(name: s) {
            interfaces {
                classes "calculator.$s"
                artifact ref:'service-dl'
            }
            implementation(class: "calculator.service.${s}Impl") {
                artifact ref:'service'
            }
            maintain 1
        }
    }
}

