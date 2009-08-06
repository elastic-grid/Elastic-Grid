deployment(name:'Calculator 1') {
    groups 'elastic-grid'

    resources id:'impl.jars', 'calculator1_0.9.2/lib/calculator1-0.9.2-impl.jar'
    resources id:'client.jars', 'calculator1_0.9.2/lib/calculator1-0.9.2-dl.jar'

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
        }
        maintain 1
    }

    ['Add', 'Subtract', 'Multiply', 'Divide'].each { s ->
        service(name: s) {
            interfaces {
                classes "calculator.$s"
                resources ref:'client.jars'
            }
            implementation(class: "calculator.service.${s}Impl") {
                resources ref:'impl.jars'
            }
          /*
            sla(id: 'CPU', low:0.2, high:0.8) {
              policy type: 'scaling', max: 3, lowerDampener: 3000, upperDampener: 3000
            }
          */
            maintain 1
        }
    }
}

