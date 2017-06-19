package com.iber.RulesViaDB;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.drools.template.jdbc.ResultSetGenerator;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
@SuppressWarnings("deprecation")
public class ResultSetGeneratorTest {
      public static void main(String[] args) {
            try {
                  testResultSet();
            } catch (Exception e) {
                  e.printStackTrace();
            }
      }
      public static void testResultSet() throws Exception {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(
                         "jdbc:postgresql://localhost:5432/drools_via_db",
                         "postgres", "asdf#");
            Statement sta = conn.createStatement();
            String sql="SELECT id, min_age, max_age, status FROM age_rules";
            ResultSet rs = sta.executeQuery(sql);
            final ResultSetGenerator converter = new ResultSetGenerator();
            final String drl = converter.compile(rs, getRulesStream());
            System.out.println(drl);
            sta.close();
            KnowledgeBuilder kbuilder =
                      KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()),
                           ResourceType.DRL);
            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
            StatefulKnowledgeSession kSession = 
               kbase.newStatefulKnowledgeSession();
            // now create some test data
            Person a = new Person("A", 22);     
            Person b = new Person("B", 52);
            Person c = new Person("C", 61);
            kSession.insert(a); 
            kSession.insert(b);   
            kSession.insert(c);
            kSession.fireAllRules();
            System.out.println(a.getName() + "," + a.getStatus());
            System.out.println(b.getName() + "," + b.getStatus());
            System.out.println(c.getName() + "," + c.getStatus());
            kSession.destroy();      
            kSession.dispose();
            kSession = kbase.newStatefulKnowledgeSession();
            Person aa = new Person("AA", 82); 
            Person bb = new Person("BB", 2);
            Person cc = new Person("CC", 41);
            kSession.insert(aa);
            kSession.insert(bb);    
            kSession.insert(cc);
            kSession.fireAllRules();
            System.out.println(aa.getName() + "," + aa.getStatus());
            System.out.println(bb.getName() + "," + bb.getStatus());
            System.out.println(cc.getName() + "," + cc.getStatus());
      }

      private static InputStream getRulesStream() throws FileNotFoundException {
    	  return new FileInputStream("/Users/sathishkumarnatarajan/Workspace/EclipseNeonWorkspace/RulesViaDB/src/main/java/src/main/resources/rules/age_Rules.drt");
      }

      @SuppressWarnings("unused")
	private static void dbOperation(String expression, Connection conn)
        throws SQLException {
            Statement st;
            st = conn.createStatement();
            int i = st.executeUpdate(expression);
            if (i == -1) {
                  System.out.println("db error : " + expression);
            }
            st.close();
      }
}