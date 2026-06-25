package net.spudacious5705.abovethecloudstweaks.particles;

import net.minecraft.client.Camera;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ParticleUtils {

    public static boolean isFacingCamera(Quaternionf quadRotation, Quaternionf cameraRotation) {
        // 1. Get the forward vector of the quad by rotating a default forward vector (0, 0, -1)
        Vector3f quadLook = new Vector3f(0.0f, 0.0f, -1.0f).rotate(quadRotation);

        // 2. Get the forward vector of the camera using the same default forward vector
        Vector3f cameraLook = new Vector3f(0.0f, 0.0f, -1.0f).rotate(cameraRotation);

        // 3. Calculate the dot product of the two direction vectors
        float dotProduct = quadLook.dot(cameraLook);

        // If the dot product is negative, they point in opposite directions (facing each other).
        // If the dot product is positive, they point in the same direction (quad faces away from camera).
        return dotProduct < 0.0f;
    }

    public static boolean isFacingCameraFast(Quaternionf subj, Quaternionf cam) {
        // Extract the Z-axis columns (forward directions) directly from the quaternions
        float q1zX = 2.0f * (subj.x * subj.z + subj.w * subj.y);
        float q1zY = 2.0f * (subj.y * subj.z - subj.w * subj.x);
        float q1zZ = 1.0f - 2.0f * (subj.x * subj.x + subj.y * subj.y);

        float q2zX = 2.0f * (cam.x * cam.z + cam.w * cam.y);
        float q2zY = 2.0f * (cam.y * cam.z - cam.w * cam.x);
        float q2zZ = 1.0f - 2.0f * (cam.x * cam.x + cam.y * cam.y);

        // Manual dot product
        return (q1zX * q2zX + q1zY * q2zY + q1zZ * q2zZ) < 0.0f;
    }

    public static boolean isQuadFacingAwayFromCamera(Vector3f quadWorldPos, Quaternionf quadRotation, Camera camera) {
        // 1. Get a direction vector pointing from the camera to the quad
        Vector3f cameraPos = new Vector3f((float)camera.getPosition().x, (float)camera.getPosition().y, (float)camera.getPosition().z);
        Vector3f dirFromCameraToQuad = new Vector3f(quadWorldPos).sub(cameraPos).normalize();

        // 2. Get the quad's local forward vector (pointing straight out of its front face)
        // Assuming your default flat quad faces (0, 0, -1) before rotation
        Vector3f quadForward = new Vector3f(0.0f, 0.0f, -1.0f).rotate(quadRotation);

        // 3. Compute the dot product between the look-at line and the quad's face
        float dotProduct = quadForward.dot(dirFromCameraToQuad);

        // If dot product > 0, the quad's face and the view line point in the same direction
        // This means the quad is facing AWAY from the camera's eyes.
        return dotProduct > 0.0f;
    }

}