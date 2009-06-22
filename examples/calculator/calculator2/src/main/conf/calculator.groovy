deployment(name:'Calculator 2') {
    groups 'calculator'

    resources id:'impl.jars', 'calculator2_0.9.1/lib/calculator2-0.9.1-impl.jar'
    resources id:'client.jars', 'calculator2_0.9.1/lib/calculator2-0.9.1-dl.jar'

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

