/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.gen;

import hapax.Template;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoader;
import hapax.TemplateResourceLoader;
import java.io.File;
import java.io.PrintWriter;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author haipn
 */
public class GenModalDAO {

    private static Logger _logger = Logger.getLogger(GenModalDAO.class);
    private static GenModalDAO _instance = null;

    private GenModalDAO() {
    }
    TemplateDictionary dict = TemplateDictionary.create();

    public static GenModalDAO getInstance() {
        if (_instance == null) {
            _instance = new GenModalDAO();
        }
        return _instance;
    }

    private void renderPrimaryKeys(TemplateDictionary dict, String[] primaryKeys) {
        dict.setVariable("first_primary_key", primaryKeys[0].split(" ")[1]);
        for (int i = 1; i < primaryKeys.length; i++) {
            TemplateDataDictionary subDict = dict.addSection("primary_key");
            subDict.setVariable("name", primaryKeys[i].split(" ")[1]);
        }
    }

    private void renderNormalCols(TemplateDictionary dict, String[] normalCols) {
        if (normalCols.length > 0) {
            dict.setVariable("first_normal_key", normalCols[0].split(" ")[1]);
            for (int i = 1; i < normalCols.length; i++) {
                TemplateDataDictionary subDict = dict.addSection("normal_key");
                subDict.setVariable("name", normalCols[i].split(" ")[1]);
            }
        }
    }

    private void renderTableCols(TemplateDictionary dict, String[] primaryKeys, String[] normalCols) {
        dict.setVariable("first_table_col", primaryKeys[0].split(" ")[1]);
        for (int i = 1; i < primaryKeys.length; i++) {
            TemplateDataDictionary subDict = dict.addSection("table_col");
            subDict.setVariable("name", primaryKeys[i].split(" ")[1]);
        }
        for (int i = 0; i < normalCols.length; i++) {
            TemplateDataDictionary subDict = dict.addSection("table_col");
            subDict.setVariable("name", normalCols[i].split(" ")[1]);
        }
    }

    private void renderTableColsWithAutoInc(TemplateDictionary dict, String[] primaryKeys, String[] normalCols) {
        dict.setVariable("first_table_col", normalCols[0].split(" ")[1]);
        for (int i = 1; i < normalCols.length; i++) {
            TemplateDataDictionary subDict = dict.addSection("table_col");
            subDict.setVariable("name", normalCols[i].split(" ")[1]);
        }
    }

    private void renderAttrs(TemplateDictionary dict,
            String[] primaryAttrs, String[] primaryTypes,
            String[] normalAttrs, String[] normalTypes) {
        for (int i = 0; i < primaryAttrs.length; i++) {
            TemplateDataDictionary subDict = dict.addSection("ATTR");
            subDict.setVariable("name", primaryAttrs[i]);
            subDict.setVariable("type", primaryTypes[i]);
        }
        for (int i = 0; i < normalAttrs.length; i++) {
            TemplateDataDictionary subDict = dict.addSection("ATTR");
            subDict.setVariable("name", normalAttrs[i]);
            subDict.setVariable("type", normalTypes[i]);
        }
    }

    private void renderConstructorRS(TemplateDictionary dict,
            String[] primaryAttrs, String[] primaryTypes, String[] primaryCols,
            String[] normalAttrs, String[] normalTypes, String[] normalCols) {
        for (int i = 0; i < primaryAttrs.length; i++) {
            TemplateDataDictionary subDict = dict.addSection("CONSTRUCTOR_RS");
            subDict.setVariable("attr", primaryAttrs[i]);
            subDict.setVariable("type", WordUtils.capitalizeFully(primaryTypes[i]));
            subDict.setVariable("table_col", primaryCols[i].split(" ")[1]);
        }
        for (int i = 0; i < normalAttrs.length; i++) {
            TemplateDataDictionary subDict = dict.addSection("CONSTRUCTOR_RS");
            subDict.setVariable("attr", normalAttrs[i]);
            subDict.setVariable("type", WordUtils.capitalizeFully(normalTypes[i]));
            subDict.setVariable("table_col", normalCols[i].split(" ")[1]);
        }
    }

    private void renderSetParams(TemplateDictionary dict,
            String[] primaryAttrs, String[] primaryTypes,
            String[] normalAttrs, String[] normalTypes) {
        for (int i = 0; i < primaryAttrs.length; i++) {
            TemplateDataDictionary subDict = dict.addSection("SET_PARAM");
            subDict.setVariable("attr", primaryAttrs[i]);
            subDict.setVariable("type", WordUtils.capitalizeFully(primaryTypes[i]));
        }
        for (int i = 0; i < normalAttrs.length; i++) {
            TemplateDataDictionary subDict = dict.addSection("SET_PARAM");
            subDict.setVariable("attr", normalAttrs[i]);
            subDict.setVariable("type", WordUtils.capitalizeFully(normalTypes[i]));
        }
    }

    private void renderSetParamsWithAutoInc(TemplateDictionary dict,
            String[] primaryAttrs, String[] primaryTypes,
            String[] normalAttrs, String[] normalTypes) {
        for (int i = 0; i < normalAttrs.length; i++) {
            TemplateDataDictionary subDict = dict.addSection("SET_PARAM");
            subDict.setVariable("attr", normalAttrs[i]);
            subDict.setVariable("type", WordUtils.capitalizeFully(normalTypes[i]));
        }
    }

    private void renderSetPrimaryKey(TemplateDictionary dict,
            String[] primaryAttrs, String[] primaryTypes) {
        for (int i = 0; i < primaryAttrs.length; i++) {
            TemplateDataDictionary subDict = dict.addSection("SET_PRIMARY_KEY");
            subDict.setVariable("attr", primaryAttrs[i]);
            subDict.setVariable("type", WordUtils.capitalizeFully(primaryTypes[i]));
        }
    }

    private void renderSetParamsForUpdate(TemplateDictionary dict,
            String[] primaryAttrs, String[] primaryTypes,
            String[] normalAttrs, String[] normalTypes) {
        for (int i = 0; i < normalAttrs.length; i++) {
            TemplateDataDictionary subDict = dict.addSection("SET_PARAM_FOR_UPDATE");
            subDict.setVariable("attr", normalAttrs[i]);
            subDict.setVariable("type", WordUtils.capitalizeFully(normalTypes[i]));
        }
        for (int i = 0; i < primaryAttrs.length; i++) {
            TemplateDataDictionary subDict = dict.addSection("SET_PARAM_FOR_UPDATE");
            subDict.setVariable("attr", primaryAttrs[i]);
            subDict.setVariable("type", WordUtils.capitalizeFully(primaryTypes[i]));
        }
    }

    private boolean writeFile(String filePath, String data) {
        try (PrintWriter writer = new PrintWriter(filePath, "UTF-8")) {
            writer.print(data);
            return true;
        } catch (Exception ex) {
            _logger.error(ex.getMessage(), ex);
        }
        return false;
    }

    public boolean renderModalDAO(String modalClassName, String entityClassName,
            String tableName, String[] primaryKeys, String[] normalCols) {
        try {
            if (primaryKeys.length <= 0) {
                return false;
            }
            TemplateDictionary dict = TemplateDictionary.create();
            dict.setVariable("modal_class_name", modalClassName);
            dict.setVariable("entity_class_name", entityClassName);
            dict.setVariable("table_name", tableName);
            renderPrimaryKeys(dict, primaryKeys);
            renderNormalCols(dict, normalCols);
            renderTableCols(dict, primaryKeys, normalCols);
            Template template = getTemplate("modal_dao_template", "template");
            String dataS = template.renderToString(dict);
            return writeFile(String.format("./src/DAO/%s.java", modalClassName), dataS);
        } catch (Exception ex) {
            _logger.error(ex.getMessage(), ex);
        }
        return false;
    }

    public boolean renderEntityDAO(String entityClassName,
            String[] primaryKeys, String[] normalCols) {
        try {
            String[] primaryAttrs = getAttrsName(primaryKeys);
            String[] primaryTypes = getAttrsType(primaryKeys);
            String[] normalAttrs = getAttrsName(normalCols);
            String[] normalTypes = getAttrsType(normalCols);
            TemplateDictionary dict = TemplateDictionary.create();
            dict.setVariable("entity_class_name", entityClassName);
            renderAttrs(dict, primaryAttrs, primaryTypes, normalAttrs, normalTypes);
            renderConstructorRS(dict, primaryAttrs, primaryTypes, primaryKeys, normalAttrs, normalTypes, normalCols);
            renderSetParamsWithAutoInc(dict, primaryAttrs, primaryTypes, normalAttrs, normalTypes);
            renderSetPrimaryKey(dict, primaryAttrs, primaryTypes);
            renderSetParamsForUpdate(dict, primaryAttrs, primaryTypes, normalAttrs, normalTypes);
            Template template = getTemplate("entity_dao_template", "template");
            String dataS = template.renderToString(dict);
            return writeFile(String.format("./src/entity/DAO/%s.java", entityClassName), dataS);
        } catch (Exception ex) {
            _logger.error(ex.getMessage(), ex);
        }
        return false;
    }

    private String[] getAttrsName(String[] colDefs) {
        String[] res = new String[colDefs.length];
        for (int i = 0; i < colDefs.length; i++) {
            String colName = colDefs[i].split(" ")[1];
            res[i] = colName.substring(0, 1).toLowerCase()
                    + WordUtils.capitalizeFully(colName.replace("_", " "))
                            .replace(" ", "").substring(1);
        }
        return res;
    }

    private String[] getAttrsType(String[] colDefs) {
        String[] res = new String[colDefs.length];
        for (int i = 0; i < colDefs.length; i++) {
            String colType = colDefs[i].split(" ")[0];
            res[i] = colType;
        }
        return res;
    }

    private Template getTemplate(String templateName, String folder) throws TemplateException {
        String apppath = System.getProperty("apppath");
        if (!apppath.endsWith("/") && !apppath.endsWith("\\")) {
            apppath += File.separator;
        }
        if (!folder.startsWith("views")) {
            folder = "views/" + folder;
        }
        if (!folder.endsWith("/") && !folder.endsWith("\\")) {
            folder += "/";
        }
        TemplateLoader templateLoader = TemplateResourceLoader.create(apppath + folder, true);
        Template template = templateLoader.getTemplate(templateName);
        return template;
    }

    public static void main(String[] args) {
        String[] primaryKeys = new String[]{"int capsule_id", "String open_id"};
        String[] normalCols = new String[]{"int total_roll_times", 
            "long first_interact_time"
        };
        getInstance().renderModalDAO("ModalCapsuleBasicInfosDAO", "CapsuleBasicInfosEntity", "event_capsule_basic_infos", primaryKeys, normalCols);
        getInstance().renderEntityDAO("CapsuleBasicInfosEntity", primaryKeys, normalCols);
    }
}
