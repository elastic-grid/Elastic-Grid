import org.rioproject.config.Constants

deployment(name:'Calculator 1') {
    groups System.getProperty(Constants.GROUPS_PROPERTY_NAME, 'elastic-grid')

    artifact id:'service',    'com.elasticgrid.examples.calculator:calculator1:0.9.3'
    artifact id:'service-dl', 'com.elasticgrid.examples.calculator:calculator1:dl:0.9.3'

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
        }
        maintain 1
    }

    ['Add', 'Subtract', 'Multiply', 'Divide'].each { s ->
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