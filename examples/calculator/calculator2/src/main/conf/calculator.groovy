deployment(name:'Calculator 2') {
    groups 'elastic-grid'

    resources id:'impl.jars', 'calculator2_0.9.2/lib/calculator2-0.9.3.jar'
    resources id:'client.jars', 'calculator2_0.9.2/lib/calculator2-0.9.3-dl.jar'

    service(name: 'Calculator') {
        interfaces {
            classes 'calculator.Calculator'
            resources ref:'client.jars'
        }
        implementation(class:'calculator.service.CalculatorImpl') {
            resources ref:'impl.jars'
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
                resources ref:'client.jars'
            }
            implementation(class: "calculator.service.${s}Impl") {
                resources ref:'impl.jars'
            }
            maintain 1
        }
    }
}

