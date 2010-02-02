package com.elasticgrid.sla.drools;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CEPEngineTest {
    private StatefulKnowledgeSession session;
    private static final String RULES_FILE = "/com/elasticgrid/sla/drools/cep-engine.drl";

    @Test
    public void testCEPEngine() {

    }

    @BeforeClass
    public void setupRulesEngine() {
        session = createSession();
    }

    private StatefulKnowledgeSession createSession() {
        KnowledgeBase kbase = loadRuleBase();
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        //KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newConsoleLogger( session );
//        session.setGlobal("services", this);
//        for (Company company : this.companies.getCompanies()) {
//            session.insert(company);
//        }
        session.fireAllRules();
        return session;
    }

    private KnowledgeBase loadRuleBase() {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        try {
            builder.add(ResourceFactory.newInputStreamResource(CEPEngineTest.class.getResourceAsStream(RULES_FILE)), ResourceType.DRL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (builder.hasErrors()) {
            System.err.println(builder.getErrors());
            System.exit(0);
        }
        KnowledgeBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption(EventProcessingOption.STREAM);
        //System.out.println(((RuleBaseConfiguration)conf).getEventProcessingMode());
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(conf);
        kbase.addKnowledgePackages(builder.getKnowledgePackages());
        return kbase;
    }

}
